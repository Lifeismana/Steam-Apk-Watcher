package com.getkeepsafe.relinker.elf;

import com.getkeepsafe.relinker.elf.Elf;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class ElfParser implements Closeable, Elf {
    private final int MAGIC = 1179403647;
    private final FileChannel channel;

    public ElfParser(File file) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File is null or does not exist");
        }
        this.channel = new FileInputStream(file).getChannel();
    }

    public Elf.Header parseHeader() throws IOException {
        this.channel.position(0L);
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(8);
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        if (readWord(byteBufferAllocate, 0L) != 1179403647) {
            throw new IllegalArgumentException("Invalid ELF Magic!");
        }
        short s = readByte(byteBufferAllocate, 4L);
        boolean z = readByte(byteBufferAllocate, 5L) == 2;
        if (s == 1) {
            return new Elf32Header(z, this);
        }
        if (s == 2) {
            return new Elf64Header(z, this);
        }
        throw new IllegalStateException("Invalid class type!");
    }

    public List<String> parseNeededDependencies() throws IOException {
        long j;
        long j2;
        this.channel.position(0L);
        ArrayList arrayList = new ArrayList();
        Elf.Header header = parseHeader();
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(8);
        byteBufferAllocate.order(header.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        long j3 = header.phnum;
        int i = 0;
        if (j3 == 65535) {
            j3 = header.getSectionHeader(0).info;
        }
        long j4 = 0;
        while (true) {
            j = 1;
            if (j4 >= j3) {
                j2 = 0;
                break;
            }
            Elf.ProgramHeader programHeader = header.getProgramHeader(j4);
            if (programHeader.type == 2) {
                j2 = programHeader.offset;
                break;
            }
            j4++;
        }
        if (j2 == 0) {
            return Collections.unmodifiableList(arrayList);
        }
        ArrayList arrayList2 = new ArrayList();
        long j5 = 0;
        while (true) {
            Elf.DynamicStructure dynamicStructure = header.getDynamicStructure(j2, i);
            long j6 = j;
            if (dynamicStructure.tag == j6) {
                arrayList2.add(Long.valueOf(dynamicStructure.val));
            } else if (dynamicStructure.tag == 5) {
                j5 = dynamicStructure.val;
            }
            i++;
            if (dynamicStructure.tag == 0) {
                break;
            }
            j = j6;
            j3 = j3;
        }
        if (j5 == 0) {
            throw new IllegalStateException("String table offset not found!");
        }
        long jOffsetFromVma = offsetFromVma(header, j3, j5);
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            arrayList.add(readString(byteBufferAllocate, ((Long) it.next()).longValue() + jOffsetFromVma));
        }
        return arrayList;
    }

    private long offsetFromVma(Elf.Header header, long j, long j2) throws IOException {
        for (long j3 = 0; j3 < j; j3++) {
            Elf.ProgramHeader programHeader = header.getProgramHeader(j3);
            if (programHeader.type == 1 && programHeader.vaddr <= j2 && j2 <= programHeader.vaddr + programHeader.memsz) {
                return (j2 - programHeader.vaddr) + programHeader.offset;
            }
        }
        throw new IllegalStateException("Could not map vma to file offset!");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.channel.close();
    }

    protected String readString(ByteBuffer byteBuffer, long j) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            long j2 = 1 + j;
            short s = readByte(byteBuffer, j);
            if (s != 0) {
                sb.append((char) s);
                j = j2;
            } else {
                return sb.toString();
            }
        }
    }

    protected long readLong(ByteBuffer byteBuffer, long j) throws IOException {
        read(byteBuffer, j, 8);
        return byteBuffer.getLong();
    }

    protected long readWord(ByteBuffer byteBuffer, long j) throws IOException {
        read(byteBuffer, j, 4);
        return byteBuffer.getInt() & 4294967295L;
    }

    protected int readHalf(ByteBuffer byteBuffer, long j) throws IOException {
        read(byteBuffer, j, 2);
        return byteBuffer.getShort() & 65535;
    }

    protected short readByte(ByteBuffer byteBuffer, long j) throws IOException {
        read(byteBuffer, j, 1);
        return (short) (byteBuffer.get() & 255);
    }

    protected void read(ByteBuffer byteBuffer, long j, int i) throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(i);
        long j2 = 0;
        while (j2 < i) {
            int i2 = this.channel.read(byteBuffer, j + j2);
            if (i2 == -1) {
                throw new EOFException();
            }
            j2 += i2;
        }
        byteBuffer.position(0);
    }
}
