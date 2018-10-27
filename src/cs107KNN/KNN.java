package cs107KNN;
import java.lang.Math; // to raise a number to the power of another number 

public class KNN {
	public static void main(String[] args) {
        String fileImages = "datasets/10-per-digit_images_train";
        String fileLabels = "datasets/10-per-digit_labels_train";

        // Extracting data from files
        byte[] dataImages = Helpers.readBinaryFile(fileImages);
        byte[] dataLabels = Helpers.readBinaryFile(fileLabels);

        // Setting tensor and labels
        byte[][][] tensor = parseIDXimages(dataImages);
        byte[] labels = parseIDXlabels(dataLabels);

        if ( tensor == null || labels == null) {
            System.exit(0);
        }

        Helpers.show("Title", tensor, labels, 2, 15);
	}

	/**
	 * Composes four bytes into an integer using big endian convention.
	 *
	 * @param bXToBY The byte containing the bits to store between positions X and Y
	 * 
	 * @return the integer having form [ b31ToB24 | b23ToB16 | b15ToB8 | b7ToB0 ]
	 */
	public static int extractInt(byte b31ToB24, byte b23ToB16, byte b15ToB8, byte b7ToB0) {
        return (((b31ToB24 & 0xFF)<< 24 ) | ((b23ToB16 & 0xFF)<< 16 ) | ((b15ToB8 & 0xFF)<< 8 ) | ((b7ToB0 & 0xFF)));
	}

	/**
	 * Parses an IDX file containing images
	 *
	 * @param data the binary content of the file
	 *
	 * @return A tensor of images
	 */
	public static byte[][][] parseIDXimages(byte[] data) {
        int nMagic = extractInt(data[0], data[1], data[2], data[3]);
        //Checking if the magic number is correct, if not 
        if (nMagic != 2051) {
            System.out.println("Error: The magic number for the images is 2049, the magic number of the file you gave is " + nMagic);
            System.out.println("Check the file you gave to parse the images");
            return null;
        }

        int nImages = extractInt(data[4], data[5], data[6], data[7]);;
        int nRow = extractInt(data[8], data[9], data[10], data[11]);;
        int nCol = extractInt(data[12], data[13], data[14], data[15]);;;

        byte[][][] parsedImages = new byte[nImages][nRow][nCol];

        System.out.print("We are dealing with "+nImages +" images that are " + nRow + " high and " + nCol + " wide.");
        System.out.println();

        for (int i = 0; i < parsedImages.length; i++) {
            for (int r = 0; r < parsedImages[i].length; r++) {
                for (int c = 0; c < parsedImages[i][r].length; c++) {
                    parsedImages[i][r][c] = (byte) ((data[16 + c + r*nRow + i*nRow*nCol] & 0xFF) - 128);
                }
            }
        }
        return parsedImages;
	}

	/**
	 * Parses an idx images containing labels
	 *
	 * @param data the binary content of the file
	 *
	 * @return the parsed labels
	 */
	public static byte[] parseIDXlabels(byte[] data) {
        int nMagic = extractInt(data[0], data[1], data[2], data[3]);
        if (nMagic != 2049) {
            System.out.println("Error: The magic number for the labels is 2051, the magic number of the file you gave is " + nMagic);
            System.out.println("Check the file you gave to parse the labels");
            return null;
        }
        int nImages = extractInt(data[4], data[5], data[6], data[7]);;

        byte[] parsedLabels = new byte[nImages];
        for (int i = 0; i < parsedLabels.length; i++) {
            parsedLabels[i] = data[8+i];
        }

		return parsedLabels;
	}

	/**
	 * @brief Computes the squared L2 distance of two images
	 * 
	 * @param a, b two images of same dimensions
	 * 
	 * @return the squared euclidean distance between the two images
	 */
	public static float squaredEuclideanDistance(byte[][] a, byte[][] b) {
		// TODO: Implémenter
		return 0f;
	}

	/**
	 * @brief Computes the inverted similarity between 2 images.
	 * 
	 * @param a b two images of same dimensions
	 * 
	 * @return the inverted similarity between the two images
	 */
	public static float invertedSimilarity(byte[][] a, byte[][] b) {
		// TODO: Implémenter
		return 0f;
	}

	/**
	 * @brief Quicksorts and returns the new indices of each value.
	 * 
	 * @param values the values whose indices have to be sorted in non decreasing
	 *               order
	 * 
	 * @return the array of sorted indices
	 * 
	 *         Example: values = quicksortIndices([3, 7, 0, 9]) gives [2, 0, 1, 3]
	 */
	public static int[] quicksortIndices(float[] values) {
		// TODO: Implémenter
		return null;
	}

	/**
	 * @brief Sorts the provided values between two indices while applying the same
	 *        transformations to the array of indices
	 * 
	 * @param values  the values to sort
	 * @param indices the indices to sort according to the corresponding values
	 * @param         low, high are the **inclusive** bounds of the portion of array
	 *                to sort
	 */
	public static void quicksortIndices(float[] values, int[] indices, int low, int high) {
		// TODO: Implémenter
	}

	/**
	 * @brief Swaps the elements of the given arrays at the provided positions
	 * 
	 * @param         i, j the indices of the elements to swap
	 * @param values  the array floats whose values are to be swapped
	 * @param indices the array of ints whose values are to be swapped
	 */
	public static void swap(int i, int j, float[] values, int[] indices) {
		// TODO: Implémenter
	}

	/**
	 * @brief Returns the index of the largest element in the array
	 * 
	 * @param array an array of integers
	 * 
	 * @return the index of the largest integer
	 */
	public static int indexOfMax(int[] array) {
		// TODO: Implémenter
		return 0;
	}

	/**
	 * The k first elements of the provided array vote for a label
	 *
	 * @param sortedIndices the indices sorted by non-decreasing distance
	 * @param labels        the labels corresponding to the indices
	 * @param k             the number of labels asked to vote
	 *
	 * @return the winner of the election
	 */
	public static byte electLabel(int[] sortedIndices, byte[] labels, int k) {
		// TODO: Implémenter
		return 0;
	}

	/**
	 * Classifies the symbol drawn on the provided image
	 *
	 * @param image       the image to classify
	 * @param trainImages the tensor of training images
	 * @param trainLabels the list of labels corresponding to the training images
	 * @param k           the number of voters in the election process
	 *
	 * @return the label of the image
	 */
	public static byte knnClassify(byte[][] image, byte[][][] trainImages, byte[] trainLabels, int k) {
		// TODO: Implémenter
		return 0;
	}

	/**
	 * Computes accuracy between two arrays of predictions
	 * 
	 * @param predictedLabels the array of labels predicted by the algorithm
	 * @param trueLabels      the array of true labels
	 * 
	 * @return the accuracy of the predictions. Its value is in [0, 1]
	 */
	public static double accuracy(byte[] predictedLabels, byte[] trueLabels) {
		// TODO: Implémenter
		return 0d;
	}
}
