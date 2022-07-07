package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase
{
    public static final String[] NBTTypes;
    private String name;

    abstract void write(final DataOutput p0) throws IOException;

    abstract void load(final DataInput p0, final int p1) throws IOException;

    protected abstract byte getId();

    NBTBase(final String par1Str) {
        if (par1Str == null) {
            this.name = "";
        }
        else {
            this.name = par1Str;
        }
    }

    NBTBase setName(final String par1Str) {
        if (par1Str == null) {
            this.name = "";
        }
        else {
            this.name = par1Str;
        }
        return this;
    }

    String getName() {
        return (this.name == null) ? "" : this.name;
    }

    public static NBTBase readNamedTag(final DataInput par0DataInput) throws IOException {
        return func_130104_b(par0DataInput, 0);
    }

    static NBTBase func_130104_b(final DataInput par0DataInput, final int par1) throws IOException {
        final byte var2 = par0DataInput.readByte();
        if (var2 == 0) {
            return new NBTTagEnd();
        }
        final String var3 = par0DataInput.readUTF();
        final NBTBase var4 = newTag(var2, var3);
        var4.load(par0DataInput, par1);
        return var4;
    }

    public static void writeNamedTag(final NBTBase par0NBTBase, final DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeByte(par0NBTBase.getId());
        if (par0NBTBase.getId() != 0) {
            par1DataOutput.writeUTF(par0NBTBase.getName());
            par0NBTBase.write(par1DataOutput);
        }
    }

    static NBTBase newTag(final byte par0, final String par1Str) {
        switch (par0) {
            case 0: {
                return new NBTTagEnd();
            }
            case 1: {
                return new NBTTagByte(par1Str);
            }
            case 2: {
                return new NBTTagShort(par1Str);
            }
            case 3: {
                return new NBTTagInt(par1Str);
            }
            case 4: {
                return new NBTTagLong(par1Str);
            }
            case 5: {
                return new NBTTagFloat(par1Str);
            }
            case 6: {
                return new NBTTagDouble(par1Str);
            }
            case 7: {
                return new NBTTagByteArray(par1Str);
            }
            case 8: {
                return new NBTTagString(par1Str);
            }
            case 9: {
                return new NBTTagList(par1Str);
            }
            case 10: {
                return new NBTTagCompound(par1Str);
            }
            case 11: {
                return new NBTTagIntArray(par1Str);
            }
            default: {
                return null;
            }
        }
    }

    static String getTagName(final byte par0) {
        switch (par0) {
            case 0: {
                return "TAG_End";
            }
            case 1: {
                return "TAG_Byte";
            }
            case 2: {
                return "TAG_Short";
            }
            case 3: {
                return "TAG_Int";
            }
            case 4: {
                return "TAG_Long";
            }
            case 5: {
                return "TAG_Float";
            }
            case 6: {
                return "TAG_Double";
            }
            case 7: {
                return "TAG_Byte_Array";
            }
            case 8: {
                return "TAG_String";
            }
            case 9: {
                return "TAG_List";
            }
            case 10: {
                return "TAG_Compound";
            }
            case 11: {
                return "TAG_Int_Array";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }

    protected abstract NBTBase copy();

    @Override
    public boolean equals(final Object par1Obj) {
        if (!(par1Obj instanceof NBTBase)) {
            return false;
        }
        final NBTBase var2 = (NBTBase)par1Obj;
        return this.getId() == var2.getId() && (this.name != null || var2.name == null) && (this.name == null || var2.name != null) && (this.name == null || this.name.equals(var2.name));
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.getId();
    }

    static {
        NBTTypes = new String[] { "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]" };
    }
}

