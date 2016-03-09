package test.uni.fmi.dsaproject.bloomfilter.bitset;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import src.uni.fmi.dsaproject.bloomfilter.bitset.BloomFilter;

public class BloomFilterTest {

	private Random r = new Random();

	@Test
	public void testConstructorBNH() throws Exception {
		System.out.println("BloomFilter(be,n,h)");

		for (int i = 0; i < 10000; i++) {
			int be = r.nextInt(20) + 1;
			int n = r.nextInt(10000) + 1;
			int h = r.nextInt(20) + 1;
			BloomFilter<String> bf = new BloomFilter<>(be, n, h);
			assertEquals(bf.getHashFunctionsCount(), h);
			assertEquals(bf.elementsCount(), 0);
			assertEquals(bf.getBitPerElement(), be);
		}
	}

	@Test
	public void testConstructorBSE() throws Exception {
		System.out.println("BloomFilter(bitsetSize,expectedNumber)");

		for (int i = 0; i < 10000; i++) {
			int b = r.nextInt(20) + 1;
			int n = r.nextInt(10000) + 1;
			BloomFilter<String> bf = new BloomFilter<>(b, n);
			assertEquals(bf.getHashFunctionsCount(),
					Math.round((b / n) * Math.log(2.0)));
			assertEquals(bf.elementsCount(), 0);
			// fail pri razlichnite bloomfilteri
			assertEquals(bf.getBitPerElement(), b / n);
		}
	}

	@Test
	public void hashFunctions() throws Exception {
		System.out.println("hash functions");
		BloomFilter<String> bf = new BloomFilter<>(64, 10);
		assertEquals(bf.getHashFunctionsCount(),
				(int) (Math.round(6.4) * Math.log(2.0)));
	}

	@Test
	public void add() throws Exception {
		System.out.println("add");

		BloomFilter<String> bf = new BloomFilter<>(64, 10);
		bf.add("evgeni");
		bf.add("sadpanda");
		assertEquals(bf.contains("evgeni"), true);
		assertEquals(bf.contains("sadpanda"), true);

	}

	@Test
	public void count() throws Exception {
		System.out.println("count");
		BloomFilter<String> bf = new BloomFilter<>(64, 10);
		assertEquals(bf.elementsCount(), 0);
		for (int i = 0; i < 10; i++) {
			bf.add(String.valueOf(i));
		}
		assertEquals(bf.elementsCount(), 10);
	}

	@Test
	public void exceptionCount() throws Exception {
		System.out.println("exception - not enough space");
		BloomFilter<String> bf = new BloomFilter<>(64, 1);
		bf.add("a");
		try {
			bf.add("b");
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Not enough space");
		}
	}

	@Test
	public void contains() throws Exception {
		System.out.println("contains");
		BloomFilter<String> bf = new BloomFilter<>(10000, 100);
		for (int i = 0; i < 100; i++) {
			bf.add("evgeni");
		}
		assertEquals(bf.contains("evgeni"), true);
		assertEquals(bf.contains("sadpanda"), false);
		bf.clear();
		assertEquals(bf.contains("evgeni"), false);
	}

	@Test
	public void clear() throws Exception {
		System.out.println("clear");

		BloomFilter<String> bf = new BloomFilter<>(10000, 100);
		for (int i = 0; i < 100; i++) {
			bf.add("evgeni");
		}
		bf.clear();
		assertEquals(bf.getHashFunctionsCount(),
				Math.round((10000 / 100) * Math.log(2.0)));
		assertEquals(bf.elementsCount(), 0);
		assertEquals(bf.getBitPerElement(), 10000 / 100, 0);
	}

	@Test
	public void falseProbability() throws Exception {
		System.out.println("false probability");
		double fb = Math.pow((1 - Math.exp(-4 * 0.0 / 64)), 4);
		BloomFilter<String> bf = new BloomFilter<>(64 / 10, 10, 4);
		assertEquals(bf.getFalsePositiveProbability(), fb, 0);
//		fb = Math.pow((1 - Math.exp(-4 * (double) 10 / (double) 64)), 4);
//		assertEquals(bf.expectedFalsePositiveProbability(), fb, 0);
	}

}
