using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using PortableDevices;
using System.Collections;
using System.IO;

namespace ServicePlatform {

	public partial class Form1 : Form {

		private System.Threading.Timer timer = null;
		private Boolean detected = false;

		private const String VERSION = "0.1";
		private const int WM_DEVICECHANGE = 0x0219;
		private const int DBT_DEVNODES_CHANGED = 0x0007;

		public Form1() {
			InitializeComponent();
		}

		private void connectWithDevice(object state) {

			if (detected)
				return;

			detected = true;

			Console.WriteLine("Device detected");

			var devices = new PortableDeviceCollection();
			devices.Refresh();

			var device = devices.First();
			device.Connect();

			Console.WriteLine("Connected to device");

			var root = device.GetContents();
			PortableDeviceFolder servicePlatformFolder = (PortableDeviceFolder)device.getServicePlatformFolder();

			if (servicePlatformFolder == null) {
				Console.WriteLine("Could not find ServicePlatform folder, have you installed ServicePlatform mobile app? Disconnecting...");
				device.Disconnect();
				return;
			}

			Console.WriteLine("Cleaning up ServicePlatform folder...");
			cleanup(device, servicePlatformFolder);

			Console.WriteLine("ServicePlatform folder detected!");
			Console.WriteLine("Which service would you like to run?");
			Console.WriteLine("> UpperCase");
			Console.WriteLine("Enter files which you would like to process (one file per line, blank line - start processing): ");

			String line, outputFolder;
			ArrayList files = new ArrayList();

			do {
				line = Console.ReadLine();
				if (!line.Equals("")) {
					files.Add(line);
				}
			} while (!line.Equals(""));

			Console.WriteLine("Detected " + files.Count + " files");
			Console.WriteLine("Enter output folder: ");
			outputFolder = Console.ReadLine();

			Console.WriteLine("Sending input params file to device...");

			if (!File.Exists("input-params")) {
				FileStream fs = File.Create("input-params");
				fs.Close();
			}
			using (StreamWriter sw = new StreamWriter(File.Open("input-params", System.IO.FileMode.Truncate))) {
				sw.WriteLine(VERSION);
				sw.WriteLine("UpperCase");
				foreach (String file in files) {
					String[] parts = file.Split('\\');
					sw.WriteLine(parts[parts.Length - 1]);
				}
			}

			device.TransferContentToDevice(@Directory.GetCurrentDirectory() + "\\input-params", servicePlatformFolder.Id);

			Console.WriteLine("Sending files to device...");

			foreach (var item in servicePlatformFolder.Files) {
				if (item is PortableDeviceFolder && item.Name.Equals("Input")) {
					foreach (String file in files) {
						device.TransferContentToDevice(@file, item.Id);
					}
					break;
				}
			}

			Console.WriteLine("Initiating processing...");

			if (!File.Exists("desktop-finished")) {
				FileStream fs = File.Create("desktop-finished");
				fs.Close();
			}
			device.TransferContentToDevice(@Directory.GetCurrentDirectory() + "\\desktop-finished", servicePlatformFolder.Id);

			Console.WriteLine("Starting processing...");
		}

		private void cleanup(PortableDevice device, PortableDeviceFolder folder) {
			foreach (var item in folder.Files) {
				if (item is PortableDeviceFile && (item.Name.Equals("input-params") || item.Name.Equals("desktop-finished"))) {
					device.DeleteFile((PortableDeviceFile)item);
				} else if (item is PortableDeviceFolder && (item.Name.Equals("Input") || item.Name.Equals("Output"))) {
					cleanupInDir(device, (PortableDeviceFolder)item);
				}
			}
		}

		private void cleanupInDir(PortableDevice device, PortableDeviceFolder folder) {
			foreach (var item in folder.Files) {
				device.DeleteFile((PortableDeviceFile)item);
			}
		}

		[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
		protected override void WndProc(ref Message m) {

			base.WndProc(ref m);

			if (m.Msg == WM_DEVICECHANGE && m.WParam.ToInt32() == DBT_DEVNODES_CHANGED) {

				if (timer != null) {
					timer.Dispose();
				}
				
				timer = new System.Threading.Timer(connectWithDevice, null, 1500, System.Threading.Timeout.Infinite);
			}
		}
	}
}
