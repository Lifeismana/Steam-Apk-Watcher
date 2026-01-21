package com.getkeepsafe.relinker.elf;

import com.getkeepsafe.relinker.elf.Elf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class Program32Header extends Elf.ProgramHeader {
    public Program32Header(ElfParser elfParser, Elf.Header header, long j) throws IOException {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(4);
        byteBufferAllocate.order(header.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        long j2 = header.phoff + (j * header.phentsize);
        this.type = elfParser.readWord(byteBufferAllocate, j2);
        this.offset = elfParser.readWord(byteBufferAllocate, 4 + j2);
        this.vaddr = elfParser.readWord(byteBufferAllocate, 8 + j2);
        this.memsz = elfParser.readWord(byteBufferAllocate, j2 + 20);
    }
}
