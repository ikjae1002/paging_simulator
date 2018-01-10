import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class main {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(new FileReader("random.txt"));
		paging page = new paging(args, scanner);
	}

}
