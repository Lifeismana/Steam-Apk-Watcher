package it.innove;

import java.nio.ByteBuffer;

/* loaded from: classes3.dex */
public class NotifyBufferContainer {
    public ByteBuffer items;

    public NotifyBufferContainer(int i) {
        this.items = ByteBuffer.allocate(i);
    }

    public void resetBuffer() {
        this.items.clear();
    }

    public byte[] put(byte[] bArr) {
        byte[] bArr2;
        if (bArr.length > this.items.remaining()) {
            int length = bArr.length - this.items.remaining();
            bArr2 = new byte[length];
            int iRemaining = this.items.remaining();
            byte[] bArr3 = new byte[iRemaining];
            System.arraycopy(bArr, 0, bArr3, 0, iRemaining);
            System.arraycopy(bArr, iRemaining, bArr2, 0, length);
            bArr = bArr3;
        } else {
            bArr2 = null;
        }
        this.items.put(bArr);
        return bArr2;
    }

    public boolean isBufferFull() {
        return this.items.remaining() == 0;
    }

    public int size() {
        return this.items.limit();
    }

    protected void finalize() throws Throwable {
        this.items = ByteBuffer.allocate(0);
    }
}
