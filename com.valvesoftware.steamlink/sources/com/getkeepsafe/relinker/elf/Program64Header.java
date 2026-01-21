package com.getkeepsafe.relinker.elf;

import com.getkeepsafe.relinker.elf.Elf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class Program64Header extends Elf.ProgramHeader {
    public Program64Header(ElfParser elfParser, Elf.Header header, long j) throws IOException {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(8);
        byteBufferAllocate.order(header.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        long j2 = header.phoff + (j * header.phentsize);
        this.type = elfParser.readWord(byteBufferAllocate, j2);
        this.offset = elfParser.readLong(byteBufferAllocate, 8 + j2);
        this.vaddr = elfParser.readLong(byteBufferAllocate, 16 + j2);
        this.memsz = elfParser.readLong(byteBufferAllocate, j2 + 40);
    }
}
