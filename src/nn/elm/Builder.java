package nn.elm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Builder {

	public int N;			// 学习样本个数
	public int M;			// 测试样本个数
	public int IN;			// 输入层神经元个数
	public int HN;			// 隐层神经元个数
	public int ON;			// 输出层神经元个数
	public double THRESHOLD;
	public int ITERATION;	// 迭代次数的上限
	public double C;
	public double gama;
	public String inputFileName;	// 数据文件路径
	
	public String mode;		// 运行模式
	
	public Builder() {
		this.build();
	}
	
	public String read(String key) {
		Properties prop = new Properties();
		String filePath = "elm.properties";
		InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
		String ret = null;
		try {
			if (is == null) {
				throw new FileNotFoundException("property file: " + filePath + " not found");
			}
			prop.load(is);
			ret = prop.getProperty(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void build() {
		this.N = Integer.parseInt(this.read("N"));
		this.M = Integer.parseInt(this.read("M"));
		this.IN = Integer.parseInt(this.read("IN"));
		this.HN = Integer.parseInt(this.read("HN"));
		this.ON = Integer.parseInt(this.read("ON"));
		this.THRESHOLD = Double.parseDouble(this.read("THRESHOLD"));
		this.ITERATION = Integer.parseInt(this.read("ITERATION"));
		this.C = Double.parseDouble(this.read("C"));
		this.gama = Double.parseDouble(this.read("gama"));
		this.inputFileName = this.read("inputFileName");
		this.mode = this.read("mode");
	}
}
