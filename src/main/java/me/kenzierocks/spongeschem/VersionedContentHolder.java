package me.kenzierocks.spongeschem;

public abstract class VersionedContentHolder {

    public static abstract class Immutable extends VersionedContentHolder {

        protected final int contentVersion;

        protected Immutable(int contentVersion) {
            this.contentVersion = contentVersion;
        }

        @Override
        public int getContentVersion() {
            return this.contentVersion;
        }

    }

    public static abstract class Mutable extends VersionedContentHolder {

        protected int contentVersion;

        protected Mutable(int contentVersion) {
            this.contentVersion = contentVersion;
        }

        @Override
        public int getContentVersion() {
            return this.contentVersion;
        }

        public void setContentVersion(int contentVersion) {
            this.contentVersion = contentVersion;
        }

    }

    public abstract int getContentVersion();

}
