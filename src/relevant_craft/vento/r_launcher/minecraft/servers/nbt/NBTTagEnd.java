package relevant_craft.vento.r_launcher.minecraft.servers.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
    public NBTTagEnd() {
        super(null);
    }

    @Override
    void load(final DataInput par1DataInput, final int par2) throws IOException {
    }

    @Override
    void write(final DataOutput par1DataOutput) throws IOException {
    }

    public byte getId() {
        return 0;
    }

    @Override
    public String toString() {
        return "END";
    }

    public NBTBase copy() {
        return new NBTTagEnd();
    }
}
