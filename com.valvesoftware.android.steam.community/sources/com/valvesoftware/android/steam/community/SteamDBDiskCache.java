package com.valvesoftware.android.steam.community;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class SteamDBDiskCache {
    protected File m_dir;
    private int m_idxTmpFileName = 0;

    /* loaded from: classes.dex */
    public interface IDataDiskAccess {
        void onRead(FileInputStream fileInputStream, long j) throws IOException;

        void onWrite(OutputStream outputStream) throws IOException;
    }

    protected String getFileNameFromUri(String str) {
        return str;
    }

    /* loaded from: classes.dex */
    private static class DataDiskAccessByteArray implements IDataDiskAccess {
        byte[] m_data;

        private DataDiskAccessByteArray() {
        }

        @Override // com.valvesoftware.android.steam.community.SteamDBDiskCache.IDataDiskAccess
        public void onWrite(OutputStream outputStream) throws IOException {
            byte[] bArr = this.m_data;
            if (bArr != null) {
                outputStream.write(bArr);
            }
        }

        @Override // com.valvesoftware.android.steam.community.SteamDBDiskCache.IDataDiskAccess
        public void onRead(FileInputStream fileInputStream, long j) throws IOException {
            byte[] bArr = new byte[(int) j];
            int read = fileInputStream.read(bArr);
            if (read != j) {
                throw new IOException("File read produced " + read + " when " + j + " was expected");
            }
            this.m_data = bArr;
        }
    }

    protected SteamDBDiskCache(File file) {
        this.m_dir = file;
    }

    public void Write(String str, byte[] bArr) {
        DataDiskAccessByteArray dataDiskAccessByteArray = new DataDiskAccessByteArray();
        dataDiskAccessByteArray.m_data = bArr;
        Write(str, dataDiskAccessByteArray);
    }

    public void Write(String str, IDataDiskAccess iDataDiskAccess) {
        try {
            WriteToFile(new File(this.m_dir, getFileNameFromUri(str)), iDataDiskAccess);
        } catch (Exception unused) {
        }
    }

    public byte[] Read(String str) {
        DataDiskAccessByteArray dataDiskAccessByteArray = new DataDiskAccessByteArray();
        if (Read(str, dataDiskAccessByteArray)) {
            return dataDiskAccessByteArray.m_data;
        }
        return null;
    }

    public boolean Read(String str, IDataDiskAccess iDataDiskAccess) {
        try {
            return ReadFromFile(new File(this.m_dir, getFileNameFromUri(str)), iDataDiskAccess);
        } catch (Exception unused) {
            return false;
        }
    }

    private synchronized boolean ReadFromFile(File file, IDataDiskAccess iDataDiskAccess) throws IOException {
        if (!file.exists()) {
            return false;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            long length = file.length();
            if (length <= 2147483647L) {
                if (iDataDiskAccess != null) {
                    iDataDiskAccess.onRead(fileInputStream, length);
                }
                return true;
            }
            throw new IOException("File " + file.getAbsolutePath() + " is too large: " + length);
        } finally {
            fileInputStream.close();
        }
    }

    private synchronized void WriteToFile(File file, IDataDiskAccess iDataDiskAccess) throws IOException {
        FileOutputStream fileOutputStream;
        this.m_idxTmpFileName++;
        File file2 = new File(this.m_dir, "tmpfile" + this.m_idxTmpFileName);
        try {
            fileOutputStream = new FileOutputStream(file2);
            if (iDataDiskAccess != null) {
                try {
                    iDataDiskAccess.onWrite(fileOutputStream);
                } catch (Throwable th) {
                    th = th;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            file2.renameTo(file);
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream = null;
        }
    }

    /* loaded from: classes.dex */
    public static class IndefiniteCache extends SteamDBDiskCache {
        public IndefiniteCache(File file) {
            super(file);
        }

        public synchronized void Delete(String str) {
            new File(this.m_dir, getFileNameFromUri(str)).delete();
        }
    }
}
