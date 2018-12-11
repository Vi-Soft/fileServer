package com.visoft.modules.docMng.ipfs;

import java.io.IOException;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

/**
 * @author vlad
 *
 */
public class IPFSUtil {

	// connection to IPFS on localhost
	public static final IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

	/**
	 * @param ipfs
	 * @param fileContentHash
	 * @return
	 * @throws IOException
	 */
	public static byte[] cat(final String fileContentHash) throws IOException {
		Multihash multihash = Multihash.fromBase58(fileContentHash);
		return cat(multihash);
	}

	/**
	 * @param ipfs
	 * @param multihash
	 * @return
	 * @throws IOException
	 */
	public static byte[] cat(final Multihash multihash) throws IOException {
		return ipfs.cat(multihash);
	}

	/**
	 * add and pin file to IPFS
	 * 
	 * @param ipfs
	 * @param fileName
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static MerkleNode add(final String fileName, final byte[] content)
			throws IOException {
		NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(
				fileName, content);
		return ipfs.add(file).get(0);
	}
}
