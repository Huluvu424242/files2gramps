package com.github.funthomas424242.files2gramps.gzip;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GZipper implements Runnable {

    final protected Logger logger = LoggerFactory
        .getLogger(getClass().getName());

    /**
     * 
     */
    final BufferedInputStream fInStream;
    final PipedOutputStream pOut;
    final PipedInputStream pIn;
    final GzipCompressorOutputStream gzOut;
    final GzipCompressorInputStream gzIn;

    public GZipper(final FileInputStream fInStream) throws IOException {
        this.fInStream = new BufferedInputStream(fInStream);
        // Thread: finInStram -> gzOut
        this.pIn = new PipedInputStream();
        this.pOut = new PipedOutputStream(pIn);
        this.gzOut = new GzipCompressorOutputStream(pOut);
        this.gzIn = new GzipCompressorInputStream(pIn, false);
    }

    @Override
    public void run() {
        try {
            IOUtils.copy(fInStream, gzOut);
            Thread.yield();
            gzOut.flush();
            gzOut.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public GzipCompressorInputStream getGzipCompressorInputStream() {
        return gzIn;
    }

}