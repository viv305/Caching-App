package com.cache;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class FileSystem<K extends Serializable, V extends Serializable> implements ProxyCache<K, V> {
	private static final Logger LOG = LoggerFactory.getLogger(FileSystem.class);
	
    private final Map<K, String> objectsStorage;
    private Path tempDir;
    private int capacity;

    FileSystem() {
        try {
			this.tempDir = Files.createTempDirectory("cache");
			this.tempDir.toFile().deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        this.objectsStorage = new ConcurrentHashMap<>();
    }

    FileSystem(int capacity) {
        try {
			this.tempDir = Files.createTempDirectory("cache");
			this.tempDir.toFile().deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        this.capacity = capacity;
        this.objectsStorage = new ConcurrentHashMap<>(capacity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized V get(K key) {
        if (isObjectPresent(key)) {
            String fileName = objectsStorage.get(key);
            try (FileInputStream fileInputStream = new FileInputStream(new File(tempDir + File.separator + fileName));
            	ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (V) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                LOG.error(format("Can't read a file. %s: %s", fileName, e.getMessage()));
            }
        }
        LOG.debug(format("Object with key '%s' does not exist", key));
        return null;
    }

    @Override
    public synchronized void put(K key, V value) {
        File tmpFile = null;
		try {
			tmpFile = Files.createTempFile(tempDir, "", "").toFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
            outputStream.writeObject(value);
            outputStream.flush();
            objectsStorage.put(key, tmpFile.getName());
        } catch (IOException e) {
            LOG.error("Can't write an object to a file " + tmpFile.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public synchronized void remove(K key) {
        String fileName = objectsStorage.get(key);
        File deletedFile = new File(tempDir + File.separator + fileName);
        if (deletedFile.delete()) {
            LOG.debug(format("Cache file '%s' has been deleted", fileName));
        } else {
            LOG.debug(format("Can't delete a file %s", fileName));
        }
        objectsStorage.remove(key);
    }

    @Override
    public int getSize() {
        return objectsStorage.size();
    }

    @Override
    public boolean isObjectPresent(K key) {
        return objectsStorage.containsKey(key);
    }

    @Override
    public boolean hasEmptyPlace() {
        return getSize() < this.capacity;
    }

    @Override
    public void clear() {
        try {
			Files.walk(tempDir)
			        .filter(Files::isRegularFile)
			        .map(Path::toFile)
			        .forEach(file -> {
			            if (file.delete()) {
			                LOG.debug(format("Cache file '%s' has been deleted", file));
			            } else {
			                LOG.error(format("Can't delete a file %s", file));
			            }
			        });
		} catch (IOException e) {
			e.printStackTrace();
		}
        objectsStorage.clear();
    }
}
