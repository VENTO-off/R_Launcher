package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase {
    public byte data;

    public NBTTagByte(final String par1Str) {
        super(par1Str);
    }

    public NBTTagByte(final String par1Str, final byte par2) {
        super(par1Str);
        this.data = par2;
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeByte(this.data);
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
        this.data = par1DataInput.readByte();
    }

    public byte getId() {
        return 1;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    public NBTBase copy() {
        return new NBTTagByte(this.getName(), this.data);
    }

    @Override
    public boolean equals(final Object par1Obj) {
        if (super.equals(par1Obj)) {
            final NBTTagByte var2 = (NBTTagByte) par1Obj;
            return this.data == var2.data;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }
}
