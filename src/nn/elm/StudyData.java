package nn.elm;

public class StudyData {

	public double[] input;
	public double teach;
	public int IN;
	
	public StudyData(int IN) {
		this.IN = IN;
		this.input = new double[IN];
	}
}
