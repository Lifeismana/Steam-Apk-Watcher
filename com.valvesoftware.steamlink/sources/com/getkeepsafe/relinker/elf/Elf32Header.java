package com.getkeepsafe.relinker.elf;

import com.getkeepsafe.relinker.elf.Elf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* loaded from: classes.dex */
public class Elf32Header extends Elf.Header {
    private final ElfParser parser;

    public Elf32Header(boolean z, ElfParser elfParser) throws IOException {
        this.bigEndian = z;
        this.parser = elfParser;
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(4);
        byteBufferAllocate.order(z ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        this.type = elfParser.readHalf(byteBufferAllocate, 16L);
        this.phoff = elfParser.readWord(byteBufferAllocate, 28L);
        this.shoff = elfParser.readWord(byteBufferAllocate, 32L);
        this.phentsize = elfParser.readHalf(byteBufferAllocate, 42L);
        this.phnum = elfParser.readHalf(byteBufferAllocate, 44L);
        this.shentsize = elfParser.readHalf(byteBufferAllocate, 46L);
        this.shnum = elfParser.readHalf(byteBufferAllocate, 48L);
        this.shstrndx = elfParser.readHalf(byteBufferAllocate, 50L);
    }

    @Override // com.getkeepsafe.relinker.elf.Elf.Header
    public Elf.SectionHeader getSectionHeader(int i) throws IOException {
        return new Section32Header(this.parser, this, i);
    }

    @Override // com.getkeepsafe.relinker.elf.Elf.Header
    public Elf.ProgramHeader getProgramHeader(long j) throws IOException {
        return new Program32Header(this.parser, this, j);
    }

    @Override // com.getkeepsafe.relinker.elf.Elf.Header
    public Elf.DynamicStructure getDynamicStructure(long j, int i) throws IOException {
        return new Dynamic32Structure(this.parser, this, j, i);
    }
}
