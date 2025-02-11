package org.kde.necessitas.ministro;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IMinistroCallback extends IInterface {
    public static final String DESCRIPTOR = "org.kde.necessitas.ministro.IMinistroCallback";

    public static class Default implements IMinistroCallback {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // org.kde.necessitas.ministro.IMinistroCallback
        public void loaderReady(Bundle bundle) throws RemoteException {
        }
    }

    void loaderReady(Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements IMinistroCallback {
        static final int TRANSACTION_loaderReady = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IMinistroCallback.DESCRIPTOR);
        }

        public static IMinistroCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(IMinistroCallback.DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IMinistroCallback)) {
                return (IMinistroCallback) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IMinistroCallback.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IMinistroCallback.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                loaderReady((Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IMinistroCallback {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IMinistroCallback.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // org.kde.necessitas.ministro.IMinistroCallback
            public void loaderReady(Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(IMinistroCallback.DESCRIPTOR);
                    _Parcel.writeTypedObject(obtain, bundle, 0);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }

    public static class _Parcel {
        /* JADX INFO: Access modifiers changed from: private */
        public static <T> T readTypedObject(Parcel parcel, Parcelable.Creator<T> creator) {
            if (parcel.readInt() != 0) {
                return creator.createFromParcel(parcel);
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T extends Parcelable> void writeTypedObject(Parcel parcel, T t, int i) {
            if (t != null) {
                parcel.writeInt(1);
                t.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
        }
    }
}
