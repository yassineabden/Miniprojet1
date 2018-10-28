package cs107KNN;
import java.lang.Math; // to raise a number to the power of another number 
import java.util.Arrays;

public class KNN {
	public static void main(String[] args) {
        int TESTS = 1000;
        int K = 7;
        int TRAIN_SIZE = 5000;

        String TRAIN_IMAGE_PATH = "datasets/"+TRAIN_SIZE+"-per-digit_images_train";
        String TRAIN_LABEL_PATH = "datasets/"+TRAIN_SIZE+"-per-digit_labels_train";

        String TEST_IMAGE_PATH = "datasets/10k_images_test";
        String TEST_LABEL_PATH = "datasets/10k_labels_test";

        byte[][][] trainImages = parseIDXimages(Helpers.readBinaryFile(TRAIN_IMAGE_PATH));
        byte[] trainLabels = parseIDXlabels(Helpers.readBinaryFile(TRAIN_LABEL_PATH));

        assert trainLabels != null && trainImages != null;

        byte[][][] testImages = parseIDXimages(Helpers.readBinaryFile(TEST_IMAGE_PATH));
        byte[] testLabels = parseIDXlabels(Helpers.readBinaryFile(TEST_LABEL_PATH));

        assert testLabels != null && testImages != null;

        byte[] predictions = new byte[TESTS];
        long start = System.currentTimeMillis();
        float perc;
        int eta = 0;
        for (int i = 0; i < TESTS; i++) {
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            perc = (i * 100.0f) / TESTS;
            long now = System.currentTimeMillis();
            double time = (now - start) / 1000d;
            if (i != 0) {
                eta = (int) (time*(TESTS-i))/i;
            }
            System.out.print(perc + "%  |  ETA: "+eta );
            predictions[i] = knnClassify(testImages[i], trainImages, trainLabels, K);
        }
        System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        long end = System.currentTimeMillis();
        double time = (end - start) / 1000d;
        System.out.println("Accuracy = " + accuracy(predictions , Arrays.copyOfRange(testLabels , 0, TESTS)) *100 + " %");
        System.out.println("Time = " + time + " seconds");
        System.out.println("Time per test image = " + (time / TESTS));

        //Helpers.show("Test", testImages, predictions, testLabels, 20, 35);
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
        assert data != null && data.length > 0;
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
        assert a.length > 0 && b.length > 0;

        float result = 0f;
        for (int i = 0 ; i < a.length ; i++) {
            for (int j = 0 ; j < a[i].length; j++) {
                result += Math.pow((a[i][j] - b[i][j]),2); //TODO : ask assistant if we need to put (byte) or not
            }
        }
		return result;
	}

    public static float[] moyenneImages(byte[][] a, byte[][] b){
        assert a.length == b.length;

        int height = a.length;
        int width = a[0].length;
        float[] result = new float[2];
        for (int i = 0 ; i< height; i++) {
            assert a[i].length == b[i].length;
            for (int j = 0 ; j < width ; j++) {
                result[0] += a[i][j];
                result[1] += b[i][j];
            }
        }
        result[0] /= height*width;
        result[1] /= height*width;
        return result;
    }

	/**
	 * @brief Computes the inverted similarity between 2 images.
	 * 
	 * @param a b two images of same dimensions
	 * 
	 * @return the inverted similarity between the two images
	 */
	public static float invertedSimilarity(byte[][] a, byte[][] b) {
        assert a.length == b.length;

        float[] moyennes = moyenneImages(a, b);
        float moyA = moyennes[0];
        float moyB = moyennes[1];


        float denomA = 0f;
        float denomB = 0f;

        for (int i = 0 ; i < a.length ; i++) {
            for (int j = 0 ; j < a[i].length; j++) {
                denomA += Math.pow((a[i][j] - moyA), 2);
                denomB += Math.pow((b[i][j] - moyB), 2);
            }
        }

        float denom = (float) Math.pow((denomA * denomB), .5);

        if (denom == 0) {
            return 2;
        }

        float nom = 0f;

        for (int i = 0 ; i < a.length ; i++) {
            assert a[i].length == b[i].length;
            for (int j = 0 ; j < a[i].length; j++) {
                nom += (a[i][j] - moyA)*(b[i][j] - moyB);
            }
        }
        float result = 1 - nom/denom;

		return result;
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
        assert values.length > 0;
        int[] indices = new int[values.length];
        for (int i = 0 ; i < values.length; i++) {
            indices[i] = i;
        }

        quicksortIndices(values, indices, 0, values.length -1 );
		return indices;
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
        int l = low;
        int h = high;
        float pivot = values[l];
        while( l <= h ){
            if (values[l] < pivot) {
                l += 1;
            }
            else if (values[h] > pivot) {
                h -= 1;
            }
            else {
                swap(l, h, values, indices);
                l += 1;
                h -= 1;
            }
        }
        if (low < h ) {
            quicksortIndices(values, indices, low, h);
        }
        if (high > l) {
            quicksortIndices(values, indices, l, high);
        }
	}

	/**
	 * @brief Swaps the elements of the given arrays at the provided positions
	 * 
	 * @param         i, j the indices of the elements to swap
	 * @param values  the array floats whose values are to be swapped
	 * @param indices the array of ints whose values are to be swapped
	 */
	public static void swap(int i, int j, float[] values, int[] indices) {
        float tmpV = values[i];
        values[i] = values[j];
        values[j] = tmpV;

        int tmpI = indices[i];
        indices[i] = indices[j];
        indices[j] = tmpI;
	}

	/**
	 * @brief Returns the index of the largest element in the array
	 * 
	 * @param array an array of integers
	 * 
	 * @return the index of the largest integer
	 */
	public static int indexOfMax(int[] array) {
        assert array.length > 0;
        int max = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[max] < array[i]) {
                max = i;
            }
        }
        return max;

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
        int[] ints = new int[10];
        for (int i = 0 ; i < k ; i++) {
            ints[labels[sortedIndices[i]]] += 1;
        }
		return (byte) indexOfMax(ints);
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

        float[] distances = new float[trainImages.length];

        for (int i = 0 ; i < trainImages.length; i++) {
            distances[i] = invertedSimilarity(image, trainImages[i]);
        }

        int[] sortedIndices = quicksortIndices(distances);

		return electLabel(sortedIndices, trainLabels, k);
        
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
        double result = 0d;
        for (int i = 0; i < predictedLabels.length; i++) {
            if (predictedLabels[i] == trueLabels[i]) {
                result += 1;
            }
        }
        result /= predictedLabels.length;
        return result;
	}
}
