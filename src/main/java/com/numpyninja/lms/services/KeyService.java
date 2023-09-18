package com.numpyninja.lms.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.numpyninja.lms.exception.CryptoException;
import com.numpyninja.lms.repository.KeyRepository;

@Component
public class KeyService {

	@Autowired
	KeyRepository keyRepo;
	
	@Value("${google.credentials.file}")
	private String inputFile;
	
	private String credentialPath ;
	
	private Logger logger = LoggerFactory.getLogger(KeyService.class);

	private Key getKey() throws Exception {
		Optional<com.numpyninja.lms.entity.Key> encodedKey = keyRepo.findById(Integer.valueOf(1));
		if (!encodedKey.isPresent()) {
			throw new Exception("Key is not present");

		}
		SecretKey secret = new SecretKeySpec(encodedKey.get().getKey(), "AES");
		return secret;
	}

	//This code should be used for storing the key in the database
	//This is not used all the time but, incase key needs to be re-written to DB, it should be done through this
	public void storeKey() throws IOException {
		String inputFile = "service_account/secret";
		BufferedInputStream fis = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(inputFile);
		byte[] content = fis.readAllBytes();
		com.numpyninja.lms.entity.Key key = new com.numpyninja.lms.entity.Key();
		key.setKey(content);
		key.setId(1);
		keyRepo.save(key);
	}

	public String getCredentialsAsFile() throws Exception {
		try {
			credentialPath = doCryptoToFile(Cipher.DECRYPT_MODE, getKey());
		} catch (Exception e) {
			logger.error("Error:",e);
			throw new Exception("Failed to get credentils");
		}
		return credentialPath;
	}
	
	public void cleanup() {
		File f = new File(credentialPath);
		if(f.exists()) {
			f.delete();
		}
	}
	public InputStream getCredentialsAsStream() throws Exception {
		try {
			return doCryptoToStream(Cipher.DECRYPT_MODE, getKey());
		} catch (Exception e) {
			logger.error("Error:",e);
			throw new Exception("Failed to get credentils");
		}
	}

	// Return the decrypted file as Stream
		private InputStream doCryptoToStream(int cipherMode, Key key) throws CryptoException {
			try {
				
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(cipherMode, key);

				BufferedInputStream inputStream = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(inputFile);
				byte[] inputBytes = inputStream.readAllBytes();
				inputStream.read(inputBytes);

				byte[] outputBytes = cipher.doFinal(inputBytes);

				return new ByteArrayInputStream(outputBytes);
				
			} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
					| IllegalBlockSizeException | IOException ex) {
				logger.error("Error:", ex);
				throw new CryptoException("Error encrypting/decrypting file", ex);
			}
		}
	// Return the decrypted file
	private String doCryptoToFile(int cipherMode, Key key) throws CryptoException {
		try {
			
			File outputFile = new File(createTempFile());
			
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(cipherMode, key);

			BufferedInputStream inputStream = (BufferedInputStream) ClassLoader.getSystemResourceAsStream(inputFile);
			byte[] inputBytes = inputStream.readAllBytes();
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);

			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);
			inputStream.close();
			outputStream.close();

			return outputFile.getAbsolutePath();
			
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException | IOException ex) {
			logger.error("Error:", ex);
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	private String createTempFile() throws IOException {
		File tempDirectory = new File(new File(System.getProperty("java.io.tmpdir")), "files");
		if (!tempDirectory.exists()) {
			tempDirectory.mkdirs();
		}

		File file = new File(tempDirectory.getAbsolutePath() + "/lms.txt");
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		return file.getAbsolutePath();
	}
	
}
