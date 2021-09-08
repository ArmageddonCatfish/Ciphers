import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Cypher {

	// The number of characters in the non-extended ASCII table
	public static int NUMBER_OF_CHARACTERS = 126;
	// First 31 characters in ASCII are "unprintable"
	public static int NUMBER_OF_TRASH_CHARACTERS = 32;
	// How many characters are being represented
	public static int ARRAY_LENGTH = 94;
	public static int ELEMENT_NOT_FOUND = -1;
	public static int DECIPHERING = 0;
	public static int ENCIPHERING = 1;

	// Can encipher any direct input or .txt file using a randomly-generated cipher and can decipher
	// any such enciphered text back using the cipher originally given
	public static void main(String[] args) throws FileNotFoundException {
		int[] key = new int[ARRAY_LENGTH];
		Arrays.fill(key, -1);
		Scanner keyboard = new Scanner(System.in);
		greetUser();
		String fileName = getFileName(keyboard);
		Scanner fileToProcess = getFileScanner(keyboard, fileName);
		int nextStep = encodeOrDecode(keyboard);
		createKey(keyboard, nextStep, key);
		String userKeyString = printAndSaveKey(key);
		File inputFile = new File(createOutputFile(fileName, nextStep));
		PrintStream output = new PrintStream(inputFile);
		processFile(fileToProcess, key, userKeyString, output, nextStep);
		printEndingText(nextStep, fileName);

	}

	// Prints the intro text
	public static void greetUser() {
		System.out.println(
				"This program will either encipher or decipher a given input or file based on an inputted or generatored cipher.");
		System.out.println(
				"This program should work for any text file, but any non-English characters or uncommon symbols will only be passed along, not coded.");
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
		for (int integer = 0; integer < ARRAY_LENGTH; integer++) {
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
		for (int element = 0; element < ARRAY_LENGTH; element++) {
			// Until the element no longer equals -1...
			while (key[element] == -1) {
				// Need a +1 to account for exclusivity of nextInt upper bound
				int newInt = r.nextInt(ARRAY_LENGTH) + NUMBER_OF_TRASH_CHARACTERS + 1;
				boolean intExists = false;
				// For all element up to this one...
				for (int checkingDuplicate = 0; checkingDuplicate < element; checkingDuplicate++) {
					// If we haven't already seen a duplicate...
					if (!intExists) {
						// If the currently generated integer is already in the array, mark it as
						// already existing.
						if (key[checkingDuplicate] == newInt) {
							intExists = true;
						}
					}
				}
				// If the integer WASN'T a duplicate, save it to the current element
				if (!intExists) {
					key[element] = newInt;
				}
			}
		}
	}

	// Returns a string that contains all the elements of the key array and prints it for the user
	public static String printAndSaveKey(int[] key) {
		System.out.println(
				"The key being used will be printed below. The key will also be included in the top line of your processed file.");
		System.out.println();
		String userKey = "";
		// For each element, excluding the last one
		for (int element = 0; element < ARRAY_LENGTH - 1; element++) {
			userKey += key[element] + " ";
		}
		// Keeps from saving a space at the very end of the key by explicitly printing the very last
		// element
		userKey += key[ARRAY_LENGTH - 1];
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
	public static void processFile(Scanner fileToProcess, int[] key, String userKeyString,
			PrintStream output, int nextStep) {
		// The very top line is the user's key
		String firstLine = fileToProcess.nextLine();
		// If a key already exists, print it to output without processing
		if (firstLine.startsWith("Key used to generate this file: ")) {
			output.println(firstLine);
		}
		// If there isn't a key as first line, process the first line
		else {
			output.println("Key used to generate this file: " + userKeyString);
			processLine(firstLine, fileToProcess, key, output, nextStep);
		}
		// For each line of the file other than the top line...
		while (fileToProcess.hasNextLine()) {
			String currentLine = fileToProcess.nextLine();
			processLine(currentLine, fileToProcess, key, output, nextStep);

		}
	}

	// Method used to encipher each line
	public static void processLine(String currentLine, Scanner fileToProcess, int[] key,
			PrintStream output, int nextStep) {
		// Decoding process
		if (nextStep == 0) {
			// For each character of the line...
			for (int eachChar = 0; eachChar < currentLine.length(); eachChar++) {
				int charToProcess = (int) currentLine.charAt(eachChar);
				int currentIndex = findIndex(charToProcess, key);
				printDecipheredCharacter(currentIndex, charToProcess, output, key);
				// output.print ((char) currentIndex + 32));
			}
			output.println();
		} else {
			// For each character of the line...
			for (int eachChar = 0; eachChar < currentLine.length(); eachChar++) {
				int charToProcess = (int) currentLine.charAt(eachChar);
				printEncipheredCharacter(charToProcess, output, key);
			}
			output.println();
		}
	}

	// A method to search for which index the input integer can be found in key
	public static int findIndex(int charToProcess, int[] key) {
		for (int currentIndex = 0; currentIndex < ARRAY_LENGTH; currentIndex++) {
			if (charToProcess == key[currentIndex]) {
				return currentIndex;
			}
		}
		return -1;
	}

	// Prints the deciphered character for the given currentIndex
	public static void printDecipheredCharacter(int currentIndex, int charToProcess,
			PrintStream output, int[] key) {
		// if given invalid character, print it back without processing
		if (currentIndex == -1) {
			output.print((char) charToProcess);
		}
		// Otherwise, prints the appropriate deciphered character according to the key
		else {
			output.print((char) (currentIndex + NUMBER_OF_TRASH_CHARACTERS));
		}
	}

	public static void printEncipheredCharacter(int charToProcess, PrintStream output, int[] key) {
		try {
			output.print((char) key[charToProcess - NUMBER_OF_TRASH_CHARACTERS]);
		}
		// If an invalid character, will simply print the character back in without processing
		catch (IndexOutOfBoundsException e) {
			output.print((char) charToProcess);
		}
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