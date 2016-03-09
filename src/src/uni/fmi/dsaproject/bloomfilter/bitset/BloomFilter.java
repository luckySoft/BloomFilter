package src.uni.fmi.dsaproject.bloomfilter.bitset;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class BloomFilter<E> {
	private BitSet bitset;
	private int hashFuncs;
	private int bitPerElement;
	private int expectedNumberOfElements;
	private int elementsInBloomFilter;
	private int bitSetSize;
	private MessageDigest digestFunction;

	private long[] createHashes(byte[] data, int hashes) {
		long[] result = new long[hashes];
		int k = 0;
		byte salt = 0;
		while (k < hashes) {
			byte[] digest;
			synchronized (this.digestFunction) {
				//update digest by specific byte (salt)
				this.digestFunction.update(salt);
				salt++;
				//Performs a final update on the digest using the specified array of bytes, then completes the digest computation
				//the input to be updated before the digest is completed. digest(input)
				digest = this.digestFunction.digest(data);
			}
			for (int i = 0; i < digest.length / 4 && k < hashes; i++) {
				int h = 0;
				for (int j = (i * 4); j < (i * 4) + 4; j++) {
					//move with 8bits
					h <<= 8;
					//h = h | number & 255  ( binary operations )
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
		for (long iHash : hashes) {
			this.bitset.set(Math.abs((int) (iHash % this.bitSetSize)), true);
		}
	}

	private boolean contains(byte[] data) {
		long[] hashes = this.createHashes(data, this.getHashFunctionsCount());
		for (long iHash : hashes) {
			if (!this.bitset.get(Math.abs((int) (iHash % this.bitSetSize)))) {
				return false;
			}
		}
		return true;
	}

	public BloomFilter(int bitsPerElement, int expectedNumberOElements,
			int hashFuncs) throws Exception {
		this.bitSetSize = (int) Math.ceil(expectedNumberOElements
				* bitsPerElement);
		this.bitPerElement = bitsPerElement;
		this.expectedNumberOfElements = expectedNumberOElements;
		this.hashFuncs = hashFuncs;
		this.elementsInBloomFilter = 0;
		this.bitset = new BitSet(bitSetSize);
		try {
			//squid uses MD5 - good for bloomfilter
			//MD5 hash function
			this.digestFunction = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("MD5 could not be found");
		}
	}

	public BloomFilter(int bitSetSize, int expectedNumberOElements)
			throws Exception {
		this(bitSetSize / expectedNumberOElements, expectedNumberOElements,
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
		this.bitset.clear();
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

	public int elementsCount() {
		return this.elementsInBloomFilter;
	}

	public int getHashFunctionsCount() {
		return this.hashFuncs;
	}

	public int getBitPerElement() {
		return this.bitPerElement;
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
		if (this.bitPerElement != other.bitPerElement) {
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
		hash = 31 * hash + this.bitPerElement;
		hash = 31 * hash + this.expectedNumberOfElements;
		hash = 31 * hash + this.bitSetSize;
		hash = 31 * hash + this.hashFuncs;
		return hash;
	}
}
