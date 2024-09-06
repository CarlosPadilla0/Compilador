public class ExecApp {

	public static void main(String[] args) {
		View v1 = new View();
		Scanner m1 = new Scanner();
		Controlador c1 = new Controlador(m1, v1);
		v1.ActionListener(c1);
	}
}
