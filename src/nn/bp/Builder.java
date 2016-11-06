package nn.bp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Builder {

	public double a;		// 输出层至隐层的学习效率
	public double b;		// 隐层至输入层学习效率
	public double alpha;	// 动量系数
	public int N;			// 学习样本个数
	public int M;			// 测试样本个数
	public int IN;			// 输入层神经元个数
	public int HN;			// 隐层神经元个数
	public int ON;			// 输出层神经元个数
	public String inputFileName;	// 数据文件路径
	
	public String mode;		// 运行模式
	
//	public static void main(String[] args) {
//		Builder b = new Builder();
//		System.out.println(b.a);
//		System.out.println(b.ON);
//		System.out.println(b.inputFileName);
//	}
	
	public Builder() {
		this.build();
	}
	
	public String read(String key) {
		Properties prop = new Properties();
		String filePath = "bp.properties";
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
		this.a = Double.parseDouble(this.read("a"));
		this.b = Double.parseDouble(this.read("b"));
		this.alpha = Double.parseDouble(this.read("alpha"));
		this.N = Integer.parseInt(this.read("N"));
		this.M = Integer.parseInt(this.read("M"));
		this.IN = Integer.parseInt(this.read("IN"));
		this.HN = Integer.parseInt(this.read("HN"));
		this.ON = Integer.parseInt(this.read("ON"));
		this.inputFileName = this.read("inputFileName");
		this.mode = this.read("mode");
	}
}
