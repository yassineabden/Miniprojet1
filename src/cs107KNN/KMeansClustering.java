package cs107KNN;

import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class KMeansClustering {
	public static void main(String[] args) {

        int K = 1000;
        int maxIters = 20;

        // TODO: Adaptez les parcours
        byte[][][] images = KNN.parseIDXimages(Helpers.readBinaryFile("datasets/1000-per-digit_images_train"));
        byte[] labels = KNN.parseIDXlabels(Helpers.readBinaryFile("datasets/1000-per-digit_labels_train"));

        byte[][][] reducedImages = KMeansReduce(images, K, maxIters);

        byte[] reducedLabels = new byte[reducedImages.length];

        Helpers.show("blw", reducedImages, 25, 25);//tbd

        for (int i = 0; i < reducedLabels.length; i++) {
            reducedLabels[i] = KNN.knnClassify(reducedImages[i], images, labels, 5);
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");//tdb
            System.out.print("Classified " + (i + 1) + " / " + reducedImages.length);
        }
        Helpers.show("blw", reducedImages, reducedLabels, 15, 15); //tdb

        Helpers.writeBinaryFile("datasets/reduced10Kto1K_images", encodeIDXimages(reducedImages));
        Helpers.writeBinaryFile("datasets/reduced10Kto1K_labels", encodeIDXlabels(reducedLabels));
    }

    /**
     * @brief Encodes a tensor of images into an array of data ready to be written on a file
     * 
     * @param images the tensor of image to encode
     * 
     * @return the array of byte ready to be written to an IDX file
     */
	public static byte[] encodeIDXimages(byte[][][] images) {
		// TODO: Implémenter
        int nImgs=images.length;
        int nRows=images[0].length;
        int nCols=images[0][0].length;

        byte[] ret = new byte [4*4 + nImgs*nRows*nCols];

        encodeInt(2051, ret, 0);
        encodeInt(nImgs, ret, 4);
        encodeInt(nRows, ret, 8);
        encodeInt(nCols, ret, 12);

        int z = 16;
        for (int i = 0; i < nImgs; i++) {
            for (int r = 0 ; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    ret[z] = (byte) (images[i][c][r] & 0xFF);
                    z++;
                }
            }
        }
		return ret;
	}

    /**
     * @brief Prepares the array of labels to be written on a binary file
     * 
     * @param labels the array of labels to encode
     * 
     * @return the array of bytes ready to be written to an IDX file
     */
	public static byte[] encodeIDXlabels(byte[] labels) {
        int nLbls = labels.length;

        byte[] ret = new byte[2*4 + nLbls];

        encodeInt(2049, ret, 0);
        encodeInt(nLbls, ret, 4);

        int z = 8;
        for (int l = 0; l < nLbls; l++) {
            ret[z] = (byte) (labels[l] & 0xFF);
            z++;
        }

        return ret;
	}

    /**
     * @brief Decomposes an integer into 4 bytes stored consecutively in the destination
     * array starting at position offset
     * 
     * @param n the integer number to encode
     * @param destination the array where to write the encoded int
     * @param offset the position where to store the most significant byte of the integer,
     * the others will follow at offset + 1, offset + 2, offset + 3
     */
	public static void encodeInt(int n, byte[] destination, int offset) {
        for (int i = 0 ; i < 4; i++) {
            destination[offset+i] = (byte) ((n >> (3-i)*8) & 0xFF);
        }
	}

    /**
     * @brief Runs the KMeans algorithm on the provided tensor to return size elements.
     * 
     * @param tensor the tensor of images to reduce
     * @param size the number of images in the reduced dataset
     * @param maxIters the number of iterations of the KMeans algorithm to perform
     * 
     * @return the tensor containing the reduced dataset
     */
	public static byte[][][] KMeansReduce(byte[][][] tensor, int size, int maxIters) {
		int[] assignments = new Random().ints(tensor.length, 0, size).toArray();
		byte[][][] centroids = new byte[size][][];
		initialize(tensor, assignments, centroids);

		int nIter = 0;
        while (nIter < maxIters) {
            // Step 1: Assign points to closest centroid
            recomputeAssignments(tensor, centroids, assignments);
            System.out.println("Recomputed assignments");
            // Step 2: Recompute centroids as average of points
            recomputeCentroids(tensor, centroids, assignments);
            System.out.println("Recomputed centroids");

            System.out.println("KMeans completed iteration " + (nIter + 1) + " / " + maxIters);

            nIter++;
        }

		return centroids;
	}
    
    /**
     * Searches for the minimal number in array 
     *
     * @param array is the array with the distances between the image that is
     * analyzed and the centroids
     *
     * @return the index of the closests (minimal distance) image of the
     * centroid 
     */
    public static int indexOfMin(float[] array){
        int min = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[min] > array[i]) {
                min = i;
            }
        }
        return min;
    }

   /**
     * @brief Assigns each image to the cluster whose centroid is the closest.
     * It modifies.
     * 
     * @param tensor the tensor of images to cluster
     * @param centroids the tensor of centroids that represent the cluster of images
     * @param assignments the vector indicating to what cluster each image belongs to.
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void recomputeAssignments(byte[][][] tensor, byte[][][] centroids, int[] assignments) {
        System.out.println("Calculating distances");
        //float perc;
        int eta = 1;
        long start = System.currentTimeMillis();
        for (int i = 0; i < tensor.length; i++) {
            //perc = (i*100.0f) / tensor.length;
            long now = System.currentTimeMillis();
            double time = (now - start )/ 1000d;
            if (i != 0) {
                 eta = (int) ((time/i)*(tensor.length-i));
            }

            System.out.print("Calculating... "+i+"/" +tensor.length+" | ETA: " +String.format("%d:%02d", eta/60, eta%60) );
            float[] distances = new float[centroids.length];
            for (int c = 0 ; c < centroids.length; c++) {

                distances[c] = KNN.squaredEuclideanDistance(tensor[i], centroids[c]);
            }
            assignments[i] = indexOfMin(distances);
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        }
        System.out.println();
	}

    /**
     * Calculates the average of the given pixels
     *
     * @param pixels is the array containing the pixels to be average
     *
     * @return the average
     */
    public static byte averagePixel(byte[] pixels){

        int avr = 0;
        for (byte b : pixels) {
            //System.out.print(b + ", ");
            avr += (int) b;
        }
        avr = (avr/pixels.length);
        byte avr1 = (byte) ((avr & 0xFF) - 128);
        //System.out.println("avr = "+avr +" ---  avr1 = " + avr1);

        return (byte) avr;
    }

    /**
     * @brief Computes the centroid of each cluster by averaging the images in the cluster
     * 
     * @param tensor the tensor of images to cluster
     * @param centroids the tensor of centroids that represent the cluster of images
     * @param assignments the vector indicating to what cluster each image belongs to.
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void recomputeCentroids(byte[][][] tensor, byte[][][] centroids, int[] assignments) {
        for (int i = 0; i < centroids.length; i++) {
            ArrayList<byte[][]> assignedToCentroid = new ArrayList<byte[][]>();
            for (int j = 0; j < assignments.length; j++) {
                if (assignments[j] == i) {
                    assignedToCentroid.add(tensor[j]);
                }
            }
            if (assignedToCentroid.size() > 1){
                byte[][] recomputedCentroid = new byte[centroids[i].length][centroids[i][0].length];
                for (int r = 0; r < recomputedCentroid.length; r++) {
                    for (int c = 0; c < recomputedCentroid[r].length; c++) {
                        byte[] pixels = new byte[assignedToCentroid.size()];
                        for (int p = 0 ; p < pixels.length; p++) {
                            pixels[p] = assignedToCentroid.get(p)[r][c];
                        }
                        recomputedCentroid[r][c] = averagePixel(pixels);
                    }

                }
                centroids[i] = recomputedCentroid;
            }
        }
	}

    /**
     * Initializes the centroids and assignments for the algorithm.
     * The assignments are initialized randomly and the centroids
     * are initialized by randomly choosing images in the tensor.
     * 
     * @param tensor the tensor of images to cluster
     * @param assignments the vector indicating to what cluster each image belongs to.
     * @param centroids the tensor of centroids that represent the cluster of images
     *  if j is at position i, then image i belongs to cluster j
     */
	public static void initialize(byte[][][] tensor, int[] assignments, byte[][][] centroids) {
		Set<Integer> centroidIds = new HashSet<>();
		Random r = new Random("cs107-2018".hashCode());
		while (centroidIds.size() != centroids.length)
			centroidIds.add(r.nextInt(tensor.length));
		Integer[] cids = centroidIds.toArray(new Integer[] {});
		for (int i = 0; i < centroids.length; i++)
			centroids[i] = tensor[cids[i]];
		for (int i = 0; i < assignments.length; i++)
			assignments[i] = cids[r.nextInt(cids.length)];

	}
}
