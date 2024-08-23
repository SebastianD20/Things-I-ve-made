using NBitcoin;
using System.Threading;
using static System.Windows.Forms.LinkLabel;

namespace brute
{


    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void startSearch(object i)
        {
            var watch = new System.Diagnostics.Stopwatch();
            running.count = 0;
            watch.Start();
            
            System.Threading.Thread.Sleep(1000);
            
            if (running.run)
            {
                
                while (running.run)
                {
                    running.count++;
                    if (Convert.ToInt32(i) == 0)
                    {
                        label1.Invoke((MethodInvoker)(() => label1.Text = $"Runtime: {watch.ElapsedMilliseconds / 1000}"));
                        label2.Invoke((MethodInvoker)(() => label2.Text = $"Count: {running.count}"));
                        label3.Invoke((MethodInvoker)(() => label3.Text = $"Per/Second: {running.count / (watch.ElapsedMilliseconds / 1000)}"));
                    }

                    var pair = generatePair();

                    BitcoinSecret priv = pair.Item1;
                    BitcoinPubKeyAddress pub = pair.Item2;

                    foreach (string ListBox in listBox1.Items)
                    {
                        string key = pub.ToString();
                        string lowKey = key.ToLower();
                        string lowBox = ListBox.ToLower();
                        if (lowKey.Contains(lowBox))
                        {
                            textBox3.Invoke((MethodInvoker)(() => textBox3.Select(textBox2.TextLength + 1, 0)));
                            textBox3.Invoke((MethodInvoker)(() => textBox3.SelectedText = Environment.NewLine + "---------------------------------------------------"));
                            textBox3.Invoke((MethodInvoker)(() => textBox3.Select(textBox2.TextLength + 1, 0)));
                            textBox3.Invoke((MethodInvoker)(() => textBox3.SelectedText = Environment.NewLine + running.count + " Public(" + lowBox + "):" + pub));
                            textBox3.Invoke((MethodInvoker)(() => textBox3.Select(textBox2.TextLength + 1, 0)));
                            textBox3.Invoke((MethodInvoker)(() => textBox3.SelectedText = Environment.NewLine + running.count + " Private(" + lowBox + "): " + priv));
                        }
                    }
                }
            }           
        }

        private void button1_Click(object sender, EventArgs e)
        {
            string var;
            var = textBox1.Text;
            listBox1.Items.Add(var);
            textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
            textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Adding to search: '{var}'"));
        }

        private void button4_Click(object sender, EventArgs e)
        {
            listBox1.Items.Remove(listBox1.SelectedItem);
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (textBox2.Visible)
            {
                textBox2.Select(textBox2.TextLength + 1, 0);
                textBox2.SelectedText = Environment.NewLine + textBox1.Text;
                textBox2.SelectionStart = textBox2.TextLength;
                textBox2.ScrollToCaret();
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            var pair = generatePair();
            BitcoinSecret sec = pair.Item1;
            BitcoinPubKeyAddress pub = pair.Item2;


        }


        public Tuple<BitcoinSecret, BitcoinPubKeyAddress> generatePair()
        {
            Key privateKey = new Key();
            BitcoinSecret mainNetPrivateKey = privateKey.GetBitcoinSecret(Network.Main);

            PubKey publicKey = privateKey.PubKey;

            var publicKeyHash = publicKey.Hash;

            var mainNetAddress = publicKeyHash.GetAddress(Network.Main);

            /*
            if (textBox2.Visible)
            {
                textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + "Public:" + mainNetAddress));

            }

            if (textBox2.Visible)
            {
                textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + "Private: " + mainNetPrivateKey));
                
            }
            */
            return Tuple.Create(mainNetPrivateKey, mainNetAddress);
        }

        private void button5_Click(object sender, EventArgs e)
        {
            if (running.run)
            {
                running.run = false;
                button5.Text = "Run";
                textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Shutdown."));
            } else
            {
                running.run = true;
                button5.Text = "Stop";

                string cores_string = textBox4.Text;
                try
                {
                    int cores = int.Parse(cores_string);
                    for (int i = 0; i < cores; i++)
                    {
                        Thread trd = new Thread(new ParameterizedThreadStart(startSearch));
                        trd.IsBackground = true;
                        trd.Start(i);
                        textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                        textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Thread: '{i}' started."));
                    }
                }
                catch (FormatException)
                {
                    textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                    textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Invaid Cores '{cores_string}'"));
                }
            }

            
        }

        private void button6_Click(object sender, EventArgs e)
        {
            textBox2.Clear();
            textBox3.Clear();
        }

        private void label4_Click(object sender, EventArgs e)
        {

        }

        private void button2_Click_1(object sender, EventArgs e)
        {
            foreach (string line in listBox1.Items)
            {
                File.WriteAllText("save.txt", line);
                textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Saved '{line}'"));
            }
            
        }

        private void button3_Click_1(object sender, EventArgs e)
        {
            string lines = File.ReadAllText("save.txt");
            foreach (string line in lines.Split('\n'))
            {
                listBox1.Items.Add(line);
                textBox2.Invoke((MethodInvoker)(() => textBox2.Select(textBox2.TextLength + 1, 0)));
                textBox2.Invoke((MethodInvoker)(() => textBox2.SelectedText = Environment.NewLine + $"Imported '{line}'"));
            }
        }
    }

    static class running
    {
        public static bool run = false;
        public static int count = 0;
    }


}