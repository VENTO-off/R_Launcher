package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTBase {
    public double data;

    public NBTTagDouble(final String par1Str) {
        super(par1Str);
    }

    public NBTTagDouble(final String par1Str, final double par2) {
        super(par1Str);
        this.data = par2;
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeDouble(this.data);
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
        this.data = par1DataInput.readDouble();
    }

    public byte getId() {
        return 6;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    public NBTBase copy() {
        return new NBTTagDouble(this.getName(), this.data);
    }

    @Override
    public boolean equals(final Object par1Obj) {
        if (super.equals(par1Obj)) {
            final NBTTagDouble var2 = (NBTTagDouble) par1Obj;
            return this.data == var2.data;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final long var1 = Double.doubleToLongBits(this.data);
        return super.hashCode() ^ (int) (var1 ^ var1 >>> 32);
    }
}

