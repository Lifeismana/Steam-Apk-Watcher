package com.getkeepsafe.relinker.elf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: classes.dex */
public class Section32Header extends Elf.SectionHeader {
    public Section32Header(ElfParser elfParser, Elf.Header header, int i) throws IOException {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(4);
        byteBufferAllocate.order(header.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        this.info = elfParser.readWord(byteBufferAllocate, header.shoff + ((long) (i * header.shentsize)) + 28);
    }
}
