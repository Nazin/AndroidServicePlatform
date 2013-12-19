namespace ServicePlatform {
	partial class MainForm {
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing) {
			if (disposing && (components != null)) {
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent() {
			this.tableLayoutPanel1 = new System.Windows.Forms.TableLayoutPanel();
			this.services = new System.Windows.Forms.ComboBox();
			this.label1 = new System.Windows.Forms.Label();
			this.label2 = new System.Windows.Forms.Label();
			this.label3 = new System.Windows.Forms.Label();
			this.fileBrowser = new System.Windows.Forms.Button();
			this.outputBrowser = new System.Windows.Forms.Button();
			this.process = new System.Windows.Forms.Button();
			this.openFileDialog1 = new System.Windows.Forms.OpenFileDialog();
			this.folderBrowserDialog1 = new System.Windows.Forms.FolderBrowserDialog();
			this.tableLayoutPanel1.SuspendLayout();
			this.SuspendLayout();
			// 
			// tableLayoutPanel1
			// 
			this.tableLayoutPanel1.AutoSize = true;
			this.tableLayoutPanel1.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
			this.tableLayoutPanel1.ColumnCount = 2;
			this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
			this.tableLayoutPanel1.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 50F));
			this.tableLayoutPanel1.Controls.Add(this.services, 1, 0);
			this.tableLayoutPanel1.Controls.Add(this.label1, 0, 0);
			this.tableLayoutPanel1.Controls.Add(this.label2, 0, 1);
			this.tableLayoutPanel1.Controls.Add(this.label3, 0, 2);
			this.tableLayoutPanel1.Controls.Add(this.fileBrowser, 1, 1);
			this.tableLayoutPanel1.Controls.Add(this.outputBrowser, 1, 2);
			this.tableLayoutPanel1.Location = new System.Drawing.Point(13, 13);
			this.tableLayoutPanel1.Name = "tableLayoutPanel1";
			this.tableLayoutPanel1.RowCount = 3;
			this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 25F));
			this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 25F));
			this.tableLayoutPanel1.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 25F));
			this.tableLayoutPanel1.Size = new System.Drawing.Size(254, 75);
			this.tableLayoutPanel1.TabIndex = 0;
			// 
			// services
			// 
			this.services.Dock = System.Windows.Forms.DockStyle.Fill;
			this.services.FormattingEnabled = true;
			this.services.Items.AddRange(new object[] {
            "UpperCase",
            "GrayScale"});
			this.services.Location = new System.Drawing.Point(130, 3);
			this.services.Name = "services";
			this.services.Size = new System.Drawing.Size(121, 21);
			this.services.TabIndex = 1;
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Dock = System.Windows.Forms.DockStyle.Fill;
			this.label1.Location = new System.Drawing.Point(3, 0);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(121, 25);
			this.label1.TabIndex = 0;
			this.label1.Text = "Service";
			this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// label2
			// 
			this.label2.AutoSize = true;
			this.label2.Dock = System.Windows.Forms.DockStyle.Fill;
			this.label2.Location = new System.Drawing.Point(3, 25);
			this.label2.Name = "label2";
			this.label2.Size = new System.Drawing.Size(121, 25);
			this.label2.TabIndex = 2;
			this.label2.Text = "Files";
			this.label2.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// label3
			// 
			this.label3.AutoSize = true;
			this.label3.Dock = System.Windows.Forms.DockStyle.Fill;
			this.label3.Location = new System.Drawing.Point(3, 50);
			this.label3.Name = "label3";
			this.label3.Size = new System.Drawing.Size(121, 25);
			this.label3.TabIndex = 3;
			this.label3.Text = "Output folder";
			this.label3.TextAlign = System.Drawing.ContentAlignment.MiddleRight;
			// 
			// fileBrowser
			// 
			this.fileBrowser.Dock = System.Windows.Forms.DockStyle.Left;
			this.fileBrowser.Location = new System.Drawing.Point(130, 28);
			this.fileBrowser.Name = "fileBrowser";
			this.fileBrowser.Size = new System.Drawing.Size(75, 19);
			this.fileBrowser.TabIndex = 4;
			this.fileBrowser.Text = "Browse";
			this.fileBrowser.UseVisualStyleBackColor = true;
			this.fileBrowser.Click += new System.EventHandler(this.fileBrowser_Click);
			// 
			// outputBrowser
			// 
			this.outputBrowser.Dock = System.Windows.Forms.DockStyle.Left;
			this.outputBrowser.Location = new System.Drawing.Point(130, 53);
			this.outputBrowser.Name = "outputBrowser";
			this.outputBrowser.Size = new System.Drawing.Size(75, 19);
			this.outputBrowser.TabIndex = 5;
			this.outputBrowser.Text = "Browse";
			this.outputBrowser.UseVisualStyleBackColor = true;
			this.outputBrowser.Click += new System.EventHandler(this.outputBrowser_Click);
			// 
			// process
			// 
			this.process.Location = new System.Drawing.Point(197, 226);
			this.process.Name = "process";
			this.process.Size = new System.Drawing.Size(75, 23);
			this.process.TabIndex = 1;
			this.process.Text = "Process";
			this.process.UseVisualStyleBackColor = true;
			this.process.Click += new System.EventHandler(this.process_Click);
			// 
			// openFileDialog1
			// 
			this.openFileDialog1.FileName = "openFileDialog1";
			// 
			// MainForm
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.AutoSize = true;
			this.ClientSize = new System.Drawing.Size(284, 261);
			this.Controls.Add(this.process);
			this.Controls.Add(this.tableLayoutPanel1);
			this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedToolWindow;
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "MainForm";
			this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
			this.Text = "ServicePlatform";
			this.Load += new System.EventHandler(this.MainForm_Load);
			this.tableLayoutPanel1.ResumeLayout(false);
			this.tableLayoutPanel1.PerformLayout();
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.TableLayoutPanel tableLayoutPanel1;
		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.ComboBox services;
		private System.Windows.Forms.Label label2;
		private System.Windows.Forms.Label label3;
		private System.Windows.Forms.Button fileBrowser;
		private System.Windows.Forms.Button outputBrowser;
		private System.Windows.Forms.Button process;
		private System.Windows.Forms.OpenFileDialog openFileDialog1;
		private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog1;
	}
}

