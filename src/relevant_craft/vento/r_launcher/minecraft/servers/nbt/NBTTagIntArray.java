package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
    public int[] intArray;

    public NBTTagIntArray(final String par1Str) {
        super(par1Str);
    }

    public NBTTagIntArray(final String par1Str, final int[] par2ArrayOfInteger) {
        super(par1Str);
        this.intArray = par2ArrayOfInteger;
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeInt(this.intArray.length);
        for (int var2 = 0; var2 < this.intArray.length; ++var2) {
            par1DataOutput.writeInt(this.intArray[var2]);
        }
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
        final int var3 = par1DataInput.readInt();
        this.intArray = new int[var3];
        for (int var4 = 0; var4 < var3; ++var4) {
            this.intArray[var4] = par1DataInput.readInt();
        }
    }

    public byte getId() {
        return 11;
    }

    @Override
    public String toString() {
        return "[" + this.intArray.length + " bytes]";
    }

    public NBTBase copy() {
        final int[] var1 = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, var1, 0, this.intArray.length);
        return new NBTTagIntArray(this.getName(), var1);
    }

    @Override
    public boolean equals(final Object par1Obj) {
        if (!super.equals(par1Obj)) {
            return false;
        }
        final NBTTagIntArray var2 = (NBTTagIntArray) par1Obj;
        return (this.intArray == null && var2.intArray == null) || (this.intArray != null && Arrays.equals(this.intArray, var2.intArray));
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }
}

