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

	public partial class MainForm : Form {

		private System.Threading.Timer timer = null;
		private Boolean detected = false;

		private ArrayList files = new ArrayList();
		private String outputFolder;

		private PortableDevice device;
		private PortableDeviceFolder servicePlatformFolder;

		private const String VERSION = "0.1.2";
		private const int WM_DEVICECHANGE = 0x0219;
		private const int DBT_DEVNODES_CHANGED = 0x0007;

		public MainForm() {
			InitializeComponent();
		}

		private void MainForm_Load(object sender, EventArgs e) {
			BeginInvoke(new MethodInvoker(delegate {
				Hide();
				//connectWithDevice(sender);
			}));
			openFileDialog1.Multiselect = true;
		}

		private void disconnectWithDevice(object state) {
			detected = false;
			Console.WriteLine("Device connection lost?");
		}

		private void connectWithDevice(object state) {

			if (detected)
				return;

			detected = true;

			Console.WriteLine("Device detected");

			var devices = new PortableDeviceCollection();
			devices.Refresh();

			device = devices.First();
			device.Connect();

			Console.WriteLine("Connected to: " + device.FriendlyName);

			var root = device.GetContents();
			servicePlatformFolder = (PortableDeviceFolder)device.getServicePlatformFolder();

			if (servicePlatformFolder == null) {
				Console.WriteLine("Could not find ServicePlatform folder, have you installed ServicePlatform mobile app? Disconnecting...");
				device.Disconnect();
				return;
			}

			BeginInvoke(new MethodInvoker(delegate {
				Show();
				//MessageBox.Show("Connected to: " + device.FriendlyName);
			}));

			cleanup(device, servicePlatformFolder);

			//device.Disconnect();
		}

		private void downloadFiles(PortableDevice device, PortableDeviceFolder folder, String outputFolder) {
			foreach (var item in folder.Files) {
				if (item is PortableDeviceFile) {
					Console.WriteLine("\tDownloading: " + item.Name);
					device.DownloadFile((PortableDeviceFile)item, outputFolder);
				}
			}
		}

		private void cleanup(PortableDevice device, PortableDeviceFolder folder) {
			Console.WriteLine("Cleaning up ServicePlatform folder...");
			foreach (var item in folder.Files) {
				if (item is PortableDeviceFile && (item.Name.Equals("input-params") || item.Name.Equals("desktop-finished") || item.Name.Equals("mobile-finished"))) {
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

		public static void DisplayFolderContents(PortableDeviceFolder folder) {
			foreach (var item in folder.Files) {
				Console.WriteLine(item.Name);
				if (item is PortableDeviceFolder) {
					DisplayFolderContents((PortableDeviceFolder)item);
				}
			}
		}

		[System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
		protected override void WndProc(ref Message m) {

			base.WndProc(ref m);

			if (m.Msg == WM_DEVICECHANGE && m.WParam.ToInt32() == DBT_DEVNODES_CHANGED) {

				if (!detected) {
					if (timer != null) {
						timer.Dispose();
					}

					timer = new System.Threading.Timer(connectWithDevice, null, 2000, System.Threading.Timeout.Infinite);
				} else {
					if (timer != null) {
						timer.Dispose();
					}

					timer = new System.Threading.Timer(disconnectWithDevice, null, 2000, System.Threading.Timeout.Infinite);
				}
			}
		}

		private void fileBrowser_Click(object sender, EventArgs e) {
			DialogResult dr = openFileDialog1.ShowDialog();
			if (dr == System.Windows.Forms.DialogResult.OK) {
				files.Clear();
				files.AddRange(openFileDialog1.FileNames);
			}
		}

		private void outputBrowser_Click(object sender, EventArgs e) {
			DialogResult dr = folderBrowserDialog1.ShowDialog();
			if (dr == System.Windows.Forms.DialogResult.OK) {
				outputFolder = folderBrowserDialog1.SelectedPath;
			}
		}

		private void process_Click(object sender, EventArgs e) {

			if (services.SelectedIndex == -1) {
				showError("Please select service");
				return;
			}

			if (files.Count == 0) {
				showError("Please select files to process");
				return;
			}

			if (outputFolder == null) {
				showError("Please select output folder");
				return;
			}

			Enabled = false;

			transferInputFile(services.SelectedItem.ToString());
			transferFiles();
			initiateProcessing();
			waitForFinish();
			downloadFiles();
			openOutputFolder();
			cleanup(device, servicePlatformFolder);

			services.SelectedIndex = -1;
			outputFolder = null;
			files.Clear();

			Enabled = true;
		}

		private void showError(String error) {
			MessageBox.Show(error, "Error", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
		}

		private void transferInputFile(String serviceName) {

			Console.WriteLine("Sending input params file to device...");

			if (!File.Exists("input-params")) {
				FileStream fs = File.Create("input-params");
				fs.Close();
			}
			using (StreamWriter sw = new StreamWriter(File.Open("input-params", System.IO.FileMode.Truncate))) {
				sw.WriteLine(VERSION);
				sw.WriteLine(serviceName);
				foreach (String file in files) {
					String[] parts = file.Split('\\');
					sw.WriteLine(parts[parts.Length - 1]);
				}
			}

			device.TransferContentToDevice(@Directory.GetCurrentDirectory() + "\\input-params", servicePlatformFolder.Id);
		}

		private void transferFiles() {

			Console.WriteLine("Sending files to device...");

			foreach (var item in servicePlatformFolder.Files) {
				if (item is PortableDeviceFolder && item.Name.Equals("Input")) {
					foreach (String file in files) {
						device.TransferContentToDevice(@file, item.Id);
					}
					break;
				}
			}
		}

		private void initiateProcessing() {

			Console.WriteLine("Initiating processing...");

			if (!File.Exists("desktop-finished")) {
				FileStream fs = File.Create("desktop-finished");
				fs.Close();
			}

			device.TransferContentToDevice(@Directory.GetCurrentDirectory() + "\\desktop-finished", servicePlatformFolder.Id);
		}

		private void waitForFinish() {

			Console.WriteLine("Waiting for processing to end...");

			Boolean finished = false;

			do {

				System.Threading.Thread.Sleep(1000);
				device.refreshFolderContents(servicePlatformFolder);

				foreach (var item in servicePlatformFolder.Files) {
					if (item is PortableDeviceFile && item.Name.Equals("mobile-finished")) {
						Console.WriteLine("Processing finished!");
						finished = true;
					}
				}
			} while (!finished);
		}

		private void downloadFiles() {

			Console.WriteLine("Downloading output files...");

			foreach (var item in servicePlatformFolder.Files) {
				if (item is PortableDeviceFolder && item.Name.Equals("Output")) {
					downloadFiles(device, (PortableDeviceFolder)item, outputFolder);
				}
			}

			Console.WriteLine("Download finished!");
		}

		private void openOutputFolder() {
			System.Diagnostics.Process prc = new System.Diagnostics.Process();
			prc.StartInfo.FileName = outputFolder;
			prc.Start();
		}
	}
}
