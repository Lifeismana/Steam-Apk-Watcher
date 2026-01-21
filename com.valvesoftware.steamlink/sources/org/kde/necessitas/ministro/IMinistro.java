package org.kde.necessitas.ministro;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import org.kde.necessitas.ministro.IMinistroCallback;

/* loaded from: classes.dex */
public interface IMinistro extends IInterface {
    public static final String DESCRIPTOR = "org.kde.necessitas.ministro.IMinistro";

    public static class Default implements IMinistro {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // org.kde.necessitas.ministro.IMinistro
        public void requestLoader(IMinistroCallback iMinistroCallback, Bundle bundle) throws RemoteException {
        }
    }

    void requestLoader(IMinistroCallback iMinistroCallback, Bundle bundle) throws RemoteException;

    public static abstract class Stub extends Binder implements IMinistro {
        static final int TRANSACTION_requestLoader = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IMinistro.DESCRIPTOR);
        }

        public static IMinistro asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IMinistro.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IMinistro)) {
                return (IMinistro) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IMinistro.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IMinistro.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                requestLoader(IMinistroCallback.Stub.asInterface(parcel.readStrongBinder()), (Bundle) _Parcel.readTypedObject(parcel, Bundle.CREATOR));
                parcel2.writeNoException();
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IMinistro {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IMinistro.DESCRIPTOR;
            }

            @Override // org.kde.necessitas.ministro.IMinistro
            public void requestLoader(IMinistroCallback iMinistroCallback, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IMinistro.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iMinistroCallback);
                    _Parcel.writeTypedObject(parcelObtain, bundle, 0);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
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
