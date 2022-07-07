package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase
{
    public String data;

    public NBTTagString(final String par1Str) {
        super(par1Str);
    }

    public NBTTagString(final String par1Str, final String par2Str) {
        super(par1Str);
        this.data = par2Str;
        if (par2Str == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeUTF(this.data);
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
        this.data = par1DataInput.readUTF();
    }

    public byte getId() {
        return 8;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    public NBTBase copy() {
        return new NBTTagString(this.getName(), this.data);
    }

    @Override
    public boolean equals(final Object par1Obj) {
        if (!super.equals(par1Obj)) {
            return false;
        }
        final NBTTagString var2 = (NBTTagString)par1Obj;
        return (this.data == null && var2.data == null) || (this.data != null && this.data.equals(var2.data));
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data.hashCode();
    }
}

