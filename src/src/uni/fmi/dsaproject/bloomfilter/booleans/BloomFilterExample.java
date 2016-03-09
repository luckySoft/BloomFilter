package src.uni.fmi.dsaproject.bloomfilter.booleans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BloomFilterExample {
	private static int elementCount = 500; // Number of elements to test
	private static List<String> existingElements;
	private static List<String> nonExistingElements;

	public static void printStat(long start, long end) {
		double diff = (end - start) / 1000.0;
		System.out
				.println(diff + "s, " + (elementCount / diff) + " elements/s");
	}

	private static void generateElements() {
		final Random r = new Random();
		// Generate elements first
		existingElements = new ArrayList<>(elementCount);
		for (int i = 0; i < elementCount; i++) {
			byte[] b = new byte[200];
			r.nextBytes(b);
			existingElements.add(new String(b));
		}

		nonExistingElements = new ArrayList<>(elementCount);
		for (int i = 0; i < elementCount; i++) {
			byte[] b = new byte[200];
			r.nextBytes(b);
			nonExistingElements.add(new String(b));
		}
	}

	private static void testPerformanceWithHashes(int index) throws Exception {
		elementCount /= index;
		System.out.println("Testing performance with " + elementCount
				+ " elements and many hashing");
		BloomFilter<String> bf = new BloomFilter<String>(1_000_000,
				elementCount);
		testPerformance(bf);
	}

	private static void testPerformanceWithElements(int index) throws Exception {
		elementCount *= index;
		System.out.println("Testing performance with " + elementCount
				+ " elements");
		BloomFilter<String> bf = new BloomFilter<String>(10000 * 5 * index,
				elementCount);
		testPerformance(bf);
	}

	private static void testPerformance(BloomFilter<String> bf)
			throws Exception {

		generateElements();
		System.out.println("Hash functions: " + bf.getHashFunctionsCount());

		System.out.println();

		// Add elements
		System.out.print("add(): ");
		long start_add = System.currentTimeMillis();
		for (int i = 0; i < elementCount; i++) {
			bf.add(existingElements.get(i));
		}
		long end_add = System.currentTimeMillis();
		printStat(start_add, end_add);

		// Check for existing elements with contains()
		System.out.print("contains(), existing: ");
		long start_contains = System.currentTimeMillis();
		for (int i = 0; i < elementCount; i++) {
			bf.contains(existingElements.get(i));
		}
		long end_contains = System.currentTimeMillis();
		printStat(start_contains, end_contains);

		// Check for nonexisting elements with contains()
		System.out.print("contains(), nonexisting: ");
		long start_ncontains = System.currentTimeMillis();
		for (int i = 0; i < elementCount; i++) {
			bf.contains(nonExistingElements.get(i));
		}
		long end_ncontains = System.currentTimeMillis();
		printStat(start_ncontains, end_ncontains);

		System.out.println();
	}

	public static void main(String[] args) throws Exception {

		BloomFilter<String> bf = new BloomFilter<>(1000, 100);
		System.out
				.println("Bloom Filter with 1024/1000(bitset/boolean[]) size and 100 expected elements");
		System.out.println("Elements in the bloom filter: "
				+ bf.elementsCount());
		System.out.println("Bits per element: " + bf.getBitPerElement());
		System.out.println("Hash functions: " + bf.getHashFunctionsCount());
		System.out.println("Excepted false probability: "
				+ bf.expectedFalsePositiveProbability() + "%");
		System.out.println("False probability with no added elements: "
				+ bf.getFalsePositiveProbability() + "%");
		System.out.println("False probability with 20 added elements: "
				+ bf.getFalsePositiveProbability(20) + "%");
		System.out.println("False probability with 40 added elements: "
				+ bf.getFalsePositiveProbability(40) + "%");
		System.out.println("False probability with 60 added elements: "
				+ bf.getFalsePositiveProbability(60) + "%");
		System.out.println("False probability with 80 added elements: "
				+ bf.getFalsePositiveProbability(80) + "%");

		System.out.println();

		bf = new BloomFilter<>(64, 10);
		System.out
				.println("Bloom Filter with 64/10(bitset/boolean[]) size and 10 expected elements");
		System.out.println("Elements in the bloom filter: "
				+ bf.elementsCount());
		System.out.println("Bits per element: " + bf.getBitPerElement());
		System.out.println("Hash functions: " + bf.getHashFunctionsCount());
		System.out.println("Excepted false probability: "
				+ bf.expectedFalsePositiveProbability() + "%");
		System.out.println("False probability with no added elements: "
				+ bf.getFalsePositiveProbability() + "%");
		System.out.println("False probability with 2 added elements: "
				+ bf.getFalsePositiveProbability(2) + "%");
		System.out.println("False probability with 4 added elements: "
				+ bf.getFalsePositiveProbability(4) + "%");
		System.out.println("False probability with 6 added elements: "
				+ bf.getFalsePositiveProbability(6) + "%");
		System.out.println("False probability with 9 added elements: "
				+ bf.getFalsePositiveProbability(9) + "%");

		System.out.println();

		System.out
				.println("A larger filter will have less false positives, and a smaller one more");

		System.out.println();

		bf = new BloomFilter<>(1000, elementCount);
		System.out
				.println("Bloom Filter with 1024/1000(bitset/boolean[]) size and 500 expected elements");
		System.out.println("Elements in the bloom filter: "
				+ bf.elementsCount());
		System.out.println("Bits per element: " + bf.getBitPerElement());
		System.out.println("Hash functions: " + bf.getHashFunctionsCount());

		generateElements();

		System.out.println("Adding Random 250 Elements");
		for (int i = 0; i < elementCount / 2; i++) {
			bf.add(existingElements.get(i));
		}
		System.out.println("Added");

		System.out.println("False Probability: "
				+ bf.getFalsePositiveProbability());
		System.out.println("Elements in the bloom filter: "
				+ bf.elementsCount());

		bf.add("evgeni");
		System.out.println("Added element: evgeni");

		System.out.println("False Probability now: "
				+ bf.getFalsePositiveProbability());
		System.out.print("Check if bloom filter contains: evgeni - ");

		if (bf.contains("evgeni")) {
			System.out.println("maybe => everything is ok");
		} else {
			System.out
					.println("bloom filter says definitely not => something went wrong");
		}

		System.out.print("Check if bloom filter contains: 61769 - ");
		if (bf.contains("61769")) {
			System.out.println("maybe => something went wrong");
		} else {
			System.out
					.println("bloom filter says definitely not => everything is ok");
		}

		bf.clear();
		System.out.println("Bloom Filter is cleared");
		System.out.println("Elements in the bloom filter: "
				+ bf.elementsCount());

		System.out.println("Adding Random 500 Elements generated before");
		for (int i = 0; i < elementCount; i++) {
			bf.add(existingElements.get(i));
		}

		int counter = 0;
		System.out
				.println("Checking if all 500 existing elements gives maybe ");
		for (int i = 0; i < elementCount; i++) {
			if (bf.contains(existingElements.get(i))) {
				counter++;
			}
		}
		System.out.println(counter + " maybes");

		System.out.print("Check bloom filter is equal to himself: ");
		if (bf.equals(bf)) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}

		System.out
				.print("Check bloom filter is equal to another bloom filter(1000,500): ");
		BloomFilter<String> newBf = new BloomFilter<String>(1000, elementCount);
		if (bf.equals(newBf)) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}
		System.out.println();
		
		for (int i = 1; i <= 100; i *= 10) {
			testPerformanceWithElements(i);
		}
		elementCount = 500;
		for (int i = 1; i <= 100; i *= 5) {
			testPerformanceWithHashes(i);
		}

		System.out.println("bloom filter is cleared");

		System.out.println();

		System.out
				.println("Bloom Filter-1 has 500 expected element -> add 501 element - > exception");
		String message = null;
		try {
			bf.add("a");
		} catch (Exception e) {
			message = e.getMessage();
		} finally {
			System.out
					.println("Successfully catched an exception. Its' message: "
							+ message);
			System.out.println("Work well");
		}

	}
}
