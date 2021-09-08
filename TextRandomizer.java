import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

public class TextRandomizer {
	
	public static int ELEMENT_NOT_FOUND = -1;
	public static int DECIPHERING = 0;
	public static int ENCIPHERING = 1;
	public static String whatToProcess;

	// Can encipher any direct input or .txt file using a randomly-generated cipher and can decipher
	// any such enciphered text back using the cipher originally given
	public static void main(String[] args) throws FileNotFoundException {
		Scanner keyboard = new Scanner(System.in);
		greetUser();
		String fileName = getFileName(keyboard);
		Scanner fileToProcess = getFileScanner(keyboard, fileName);
		whatToProcess = consolidateInput(fileToProcess);
		int nextStep = encodeOrDecode(keyboard);
		int[] key = new int[whatToProcess.length()];
		preformatKey(key);
		createKey(keyboard, nextStep, key);
		String userKeyString = printAndSaveKey(key);
		File inputFile = new File(createOutputFile(fileName, nextStep));
		PrintStream output = new PrintStream(inputFile);
		processFile(key, userKeyString, output, nextStep);
		printEndingText(nextStep, fileName);

	}
	
	// Fills the array with incrementing elements starting at 0
	public static void preformatKey(int[] key) {
			for (int element = 0; element < key.length; element++) {
				key[element] = element;
			}
	}
	// Prints the intro text
	public static void greetUser() {
		System.out.println(
				"This program will either encipher or decipher a given input or file based on an inputted or generatored cipher.");
		System.out.println(
				"This program should work for any text file, with any characters in the file or input.");
		System.out.println();

	}

	// Uses user input of 0 or 1 to determine whether to encode or decode a text
	public static int encodeOrDecode(Scanner keyboard) {
		System.out.println();
		System.out.println("Would you want to decipher (0) or encipher (1)?");
		while (true) {
			System.out.print("Enter either 0 to decipher or 1 to encipher: ");
			String userInput = keyboard.nextLine();
			if (userInput.equals("0")) {
				System.out.println("Deciphering option selected.");
				return DECIPHERING;
			} else if (userInput.equals("1")) {
				System.out.println("Enciphering option selected.");
				return ENCIPHERING;
			}
			System.out.println("Invalid input!");
		}
	}

	// Finds the name of the file to be analyzed, or returns an empty string if no valid file
	public static String getFileName(Scanner keyboard) {
		System.out.print(
				"Enter the name of the file you'd like to process, EXCLUDING .txt, or enter anything else to process a direct input: ");
		String fileName = keyboard.nextLine().trim();
		File fileToProcess = new File(fileName + ".txt");
		if (fileToProcess.exists()) {
			return fileName;
		} else {
			return "";
		}
	}

	// Either finds an existing file or uses user input as the object of enciphering or deciphering
	public static Scanner getFileScanner(Scanner keyboard, String fileName)
			throws FileNotFoundException {
		// If some existing filename was input, attaches scanner to that file
		if (!fileName.equals("")) {
			System.out.println("File " + fileName + ".txt was found!");
			return new Scanner(new File(fileName + ".txt"));
		}
		// Otherwise, asks for user input and attaches scanner to what's input}
		else {
			System.out.println("No such file found.");
			System.out.print("Please enter the direct input you'd like processed: ");
			String whatToProcess = keyboard.nextLine();
			System.out.println("User input will be processed.");
			return new Scanner(whatToProcess);
		}
	}
	
	// Takes the user input or file and makes it into one large string
	public static String consolidateInput (Scanner fileToProcess) {
		String result = "";
		// Will add all the lines in a file together into one string until no more lines exist
		while (fileToProcess.hasNextLine()) {
			String line = fileToProcess.nextLine();
			// Won't use preface line as part of the consolidated input
			if (!(line.startsWith("Key used to generate this file: "))) {
				result += line;
				// Adds a line break only if there's another paragraph coming up
				if (fileToProcess.hasNextLine()) {
					result += "\n";
				}
			}
		}
		return result;
	}

	// For encoding, asks if user wants to use an existing key or new key. For decoding, asks for
	// existing key. Updates key array
	public static void createKey(Scanner keyboard, int nextStep, int[] key) {
		boolean validInputGiven = false;
		System.out.println();
		// AKA, if encoding
		if (nextStep == 1) {
			System.out.println("Would you like to use your own key (0) or generate a new one (1)?");
			while (!validInputGiven) {
				System.out.print("Enter either 0 to use your own key or 1 to generate a new one: ");
				String userInput = keyboard.nextLine();
				if (userInput.equals("0")) {
					System.out.println("Will use user's key.");
					validInputGiven = true;
					saveUserKey(keyboard, key);
				} else if (userInput.equals("1")) {
					System.out.println("Will use a newly-generated key.");
					generateNewKey(key);
					validInputGiven = true;

				}
				if (!validInputGiven) {
					System.out.println("Invalid input!");
				}
			}
		} else {
			// AKA, if decoding
			saveUserKey(keyboard, key);

		}
	}

	// Replaces the key array with user input. Not error-proofed.
	public static void saveUserKey(Scanner keyboard, int[] key) {
		System.out.print(
				"Enter your key EXACTLY as given to you upon last use, or the program will crash: ");
		String userKey = keyboard.nextLine();
		Scanner scanKey = new Scanner(userKey);
		for (int integer = 0; integer < key.length; integer++) {
			key[integer] = scanKey.nextInt();
		}
		System.out.println("Valid key!");
		scanKey.close();
	}

	// Randomly generates a new key
	public static void generateNewKey(int[] key) {
		System.out.println();
		Random r = new Random();
		System.out.println("Generating new key...");
		// For each element in the key array...
		for (int element = 0; element < key.length; element++) {
			int tempForSwap = key[element];
			// Randomly selects another element in the array to swap with
			int indexForSwap = r.nextInt(key.length);
			key[element] = key[indexForSwap];
			key[indexForSwap] = tempForSwap;
		}
	}

	// Returns a string that contains all the elements of the key array and prints it for the user
	public static String printAndSaveKey(int[] key) {
		System.out.println(
				"The key being used will be printed below. The key will also be included in the top line of your processed file.");
		System.out.println();
		String userKey = "";
		// For each element, excluding the last one
		for (int element = 0; element < key.length- 1; element++) {
			userKey += key[element] + " ";
		}
		// Keeps from saving a space at the very end of the key by explicitly printing the very last
		// element
		userKey += key[key.length - 1];
		System.out.println(userKey);
		System.out.println();
		return userKey;
	}

	// Creates an appropriately-named output file for the program to encipher or decipher to
	public static String createOutputFile(String fileName, int nextStep) {
		// Determines whether direct input was used and uses "UserInput" as root of output file name
		// if so
		if (fileName.equals("")) {
			return "UserInput" + decipherOrEncipher(nextStep) + ".txt";
		}
		// Otherwise, simply uses the original file as root of output file name
		else {
			return fileName + decipherOrEncipher(nextStep) + ".txt";
		}
	}

	// Returns either "Deciphered" or "Enciphered" depending on nextStep
	public static String decipherOrEncipher(int nextStep) {
		if (nextStep == DECIPHERING) {
			return "Deciphered";
		} else {
			return "Enciphered";
		}
	}

	// Enciphers or deciphers the given input line by line and character by character
	public static void processFile(int[] key, String userKeyString,
			PrintStream output, int nextStep) {
		// The very top line is the user's key
		Scanner fileToProcess = new Scanner(whatToProcess);
		
		//Look here!
		
		
		String firstLine = fileToProcess.nextLine();
		// If a key already exists, print it to output without processing
		if (firstLine.startsWith("Key used to generate this file: ")) {
			output.println(firstLine);
		}
		// If there isn't a key as first line, process the first line
		else {
			output.println("Key used to generate this file: " + userKeyString);
		}
			processLine(fileToProcess, key, output, nextStep);
	}

	// Method used to encipher each line
	public static void processLine(Scanner fileToProcess, int[] key,
			PrintStream output, int nextStep) {
		// Decoding process
		if (nextStep == 0) {
			// For each character of the line...
			for (int element = 0; element < key.length; element++) {
				int i = findIndex(element, key);
				output.print(whatToProcess.charAt(i));
			}
			output.println();
		} 
		// Encoding process
		else {
			// For each character of the line...
			for (int element = 0; element < key.length; element++) {
				output.print((char) whatToProcess.charAt(key[element]));
			}
			output.println();
		}
	}

	// A method to search for which index the input integer can be found in key
	public static int findIndex(int charToProcess, int[] key) {
		for (int currentIndex = 0; currentIndex < key.length; currentIndex++) {
			if (charToProcess == key[currentIndex]) {
				return currentIndex;
			}
		}
		return -1;
	}

	// Prints that the process was successful and the name of the output file
	public static void printEndingText(int nextStep, String fileName) {
		System.out.print("Program success! The following is the name of the output file: ");
		if (fileName.equals("")) {
			System.out.print("UserInput" + decipherOrEncipher(nextStep) + ".txt");
		} else {
			System.out.print(fileName + decipherOrEncipher(nextStep) + ".txt");
		}
	}
}