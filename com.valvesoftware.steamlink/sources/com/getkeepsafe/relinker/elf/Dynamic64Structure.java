package com.getkeepsafe.relinker.elf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: classes.dex */
public class Dynamic64Structure extends Elf.DynamicStructure {
    public Dynamic64Structure(ElfParser elfParser, Elf.Header header, long j, int i) throws IOException {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(8);
        byteBufferAllocate.order(header.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        long j2 = j + ((long) (i * 16));
        this.tag = elfParser.readLong(byteBufferAllocate, j2);
        this.val = elfParser.readLong(byteBufferAllocate, j2 + 8);
    }
}
