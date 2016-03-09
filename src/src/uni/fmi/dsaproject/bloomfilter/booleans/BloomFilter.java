package src.uni.fmi.dsaproject.bloomfilter.booleans;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BloomFilter<E> {

	private boolean[] bitset;
	private int hashFuncs;
	private int elementsInBloomFilter;
	private int bitsPerElement;
	private int expectedNumberOfElements;
	private int bitSetSize;
	private MessageDigest digestFunction;

	private long[] createHashes(byte[] data, int hashes) {
		long[] result = new long[hashes];
		int k = 0;
		byte salt = 0;
		while (k < hashes) {
			byte[] digest;
			synchronized (this.digestFunction) {
				this.digestFunction.update(salt);
				salt++;
				digest = this.digestFunction.digest(data);
			}
			for (int i = 0; i < digest.length / 4 && k < hashes; i++) {
				int h = 0;
				for (int j = (i * 4); j < (i * 4) + 4; j++) {
					h <<= 8;
					h |= ((int) digest[j]) & 0xFF;
				}
				result[k] = h;
				k++;
			}
		}
		return result;

	}

	private void add(byte[] data) {
		long[] hashes = this.createHashes(data, this.getHashFunctionsCount());
		int index;
		for (long iHash : hashes) {
			index = (int) (Math.abs(iHash) % this.bitSetSize);
			this.bitset[index] = true;
		}
	}

	private boolean contains(byte[] data) {
		long[] hashes = this.createHashes(data, this.getHashFunctionsCount());
		for (long iHash : hashes) {
			if (!this.bitset[(int) (Math.abs(iHash) % this.bitSetSize)] == true) {
				return false;
			}
		}
		return true;
	}

	public BloomFilter(int bitsPerElement, int expectedNumberOElements,
			int hashFuncs) throws Exception {
		this.bitSetSize = (int) Math.ceil(expectedNumberOElements
				* bitsPerElement);
		this.bitsPerElement = bitsPerElement;
		this.expectedNumberOfElements = expectedNumberOElements;
		this.hashFuncs = hashFuncs;
		this.elementsInBloomFilter = 0;
		this.bitset = new boolean[bitSetSize];
		try {
			this.digestFunction = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("MD5 could not be found");
		}
	}

	public BloomFilter(int bitSetSize, int expectedNumberOElements)
			throws Exception {
		this((bitSetSize / expectedNumberOElements), expectedNumberOElements,
				(int) Math.round((bitSetSize / expectedNumberOElements)
						* Math.log(2.0)));
	}

	public void add(E element) throws Exception {
		if (this.elementsInBloomFilter == this.expectedNumberOfElements) {
			throw new Exception("Not enough space");
		}
		this.add(element.toString().getBytes());
		this.elementsInBloomFilter++;
	}

	public boolean contains(E element) {
		return this.contains(element.toString().getBytes());
	}

	public void clear() {
		this.elementsInBloomFilter = 0;
		this.bitset = new boolean[this.bitSetSize];
	}

	public double expectedFalsePositiveProbability() {
		return getFalsePositiveProbability(this.expectedNumberOfElements);
	}

	public double getFalsePositiveProbability(double numberOfElements) {
		// (1 - e^(-k * n / m)) ^ k
		return Math
				.pow((1 - Math.exp(-this.getHashFunctionsCount()
						* (double) numberOfElements / (double) this.bitSetSize)),
						this.getHashFunctionsCount());

	}

	public double getFalsePositiveProbability() {
		return getFalsePositiveProbability(this.elementsInBloomFilter);
	}

	public int getBitPerElement() {
		return this.bitsPerElement;
	}

	public int elementsCount() {
		return this.elementsInBloomFilter;
	}

	public int getHashFunctionsCount() {
		return this.hashFuncs;
	}

	public int getBitSetSize() {
		return bitSetSize;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		BloomFilter<E> other = (BloomFilter<E>) obj;
		if (this.expectedNumberOfElements != other.expectedNumberOfElements) {
			return false;
		}
		if (this.bitsPerElement != other.bitsPerElement) {
			return false;
		}

		if (this.hashFuncs != other.hashFuncs) {
			return false;
		}
		if (this.bitSetSize != other.bitSetSize) {
			return false;
		}
		if (this.elementsInBloomFilter != other.elementsInBloomFilter) {
			return false;
		}
		if (this.bitset != other.bitset
				&& (this.bitset == null || !this.bitset.equals(other.bitset))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (this.bitset != null ? this.bitset.hashCode() : 0);
		hash = 31 * hash + (int) this.bitsPerElement;
		hash = 31 * hash + this.expectedNumberOfElements;
		hash = 31 * hash + this.bitSetSize;
		hash = 31 * hash + this.hashFuncs;
		return hash;
	}

}
