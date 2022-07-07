package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase {
    public float data;

    public NBTTagFloat(final String par1Str) {
        super(par1Str);
    }

    public NBTTagFloat(final String par1Str, final float par2) {
        super(par1Str);
        this.data = par2;
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeFloat(this.data);
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
        this.data = par1DataInput.readFloat();
    }

    public byte getId() {
        return 5;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    public NBTBase copy() {
        return new NBTTagFloat(this.getName(), this.data);
    }

    @Override
    public boolean equals(final Object par1Obj) {
        if (super.equals(par1Obj)) {
            final NBTTagFloat var2 = (NBTTagFloat) par1Obj;
            return this.data == var2.data;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }
}
