package nn.bp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Builder {

	public double a;		// ������������ѧϰЧ��
	public double b;		// �����������ѧϰЧ��
	public double alpha;	// ����ϵ��
	public int N;			// ѧϰ��������
	public int M;			// ������������
	public int IN;			// �������Ԫ����
	public int HN;			// ������Ԫ����
	public int ON;			// �������Ԫ����
	public String inputFileName;	// �����ļ�·��
	
	public String mode;		// ����ģʽ
	
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
