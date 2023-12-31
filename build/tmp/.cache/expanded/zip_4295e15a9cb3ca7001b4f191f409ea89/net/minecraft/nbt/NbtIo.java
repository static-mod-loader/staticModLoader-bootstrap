package net.minecraft.nbt;

import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.util.FastBufferedInputStream;

public class NbtIo {
   public static CompoundTag readCompressed(File p_128938_) throws IOException {
      try (InputStream inputstream = new FileInputStream(p_128938_)) {
         return readCompressed(inputstream);
      }
   }

   private static DataInputStream createDecompressorStream(InputStream p_202494_) throws IOException {
      return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(p_202494_)));
   }

   public static CompoundTag readCompressed(InputStream p_128940_) throws IOException {
      try (DataInputStream datainputstream = createDecompressorStream(p_128940_)) {
         return read(datainputstream, NbtAccounter.UNLIMITED);
      }
   }

   public static void parseCompressed(File p_202488_, StreamTagVisitor p_202489_) throws IOException {
      try (InputStream inputstream = new FileInputStream(p_202488_)) {
         parseCompressed(inputstream, p_202489_);
      }

   }

   public static void parseCompressed(InputStream p_202491_, StreamTagVisitor p_202492_) throws IOException {
      try (DataInputStream datainputstream = createDecompressorStream(p_202491_)) {
         parse(datainputstream, p_202492_);
      }

   }

   public static void writeCompressed(CompoundTag p_128945_, File p_128946_) throws IOException {
      try (OutputStream outputstream = new FileOutputStream(p_128946_)) {
         writeCompressed(p_128945_, outputstream);
      }

   }

   public static void writeCompressed(CompoundTag p_128948_, OutputStream p_128949_) throws IOException {
      try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(p_128949_)))) {
         write(p_128948_, dataoutputstream);
      }

   }

   public static void write(CompoundTag p_128956_, File p_128957_) throws IOException {
      try (
         FileOutputStream fileoutputstream = new FileOutputStream(p_128957_);
         DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
      ) {
         write(p_128956_, dataoutputstream);
      }

   }

   @Nullable
   public static CompoundTag read(File p_128954_) throws IOException {
      if (!p_128954_.exists()) {
         return null;
      } else {
         CompoundTag compoundtag;
         try (
            FileInputStream fileinputstream = new FileInputStream(p_128954_);
            DataInputStream datainputstream = new DataInputStream(fileinputstream);
         ) {
            compoundtag = read(datainputstream, NbtAccounter.UNLIMITED);
         }

         return compoundtag;
      }
   }

   public static CompoundTag read(DataInput p_128929_) throws IOException {
      return read(p_128929_, NbtAccounter.UNLIMITED);
   }

   public static CompoundTag read(DataInput p_128935_, NbtAccounter p_128936_) throws IOException {
      Tag tag = readUnnamedTag(p_128935_, 0, p_128936_);
      if (tag instanceof CompoundTag) {
         return (CompoundTag)tag;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundTag p_128942_, DataOutput p_128943_) throws IOException {
      writeUnnamedTag(p_128942_, p_128943_);
   }

   public static void parse(DataInput p_197510_, StreamTagVisitor p_197511_) throws IOException {
      TagType<?> tagtype = TagTypes.getType(p_197510_.readByte());
      if (tagtype == EndTag.TYPE) {
         if (p_197511_.visitRootEntry(EndTag.TYPE) == StreamTagVisitor.ValueResult.CONTINUE) {
            p_197511_.visitEnd();
         }

      } else {
         switch (p_197511_.visitRootEntry(tagtype)) {
            case HALT:
            default:
               break;
            case BREAK:
               StringTag.skipString(p_197510_);
               tagtype.skip(p_197510_);
               break;
            case CONTINUE:
               StringTag.skipString(p_197510_);
               tagtype.parse(p_197510_, p_197511_);
         }

      }
   }

   public static void writeUnnamedTag(Tag p_128951_, DataOutput p_128952_) throws IOException {
      p_128952_.writeByte(p_128951_.getId());
      if (p_128951_.getId() != 0) {
         p_128952_.writeUTF("");
         p_128951_.write(p_128952_);
      }
   }

   private static Tag readUnnamedTag(DataInput p_128931_, int p_128932_, NbtAccounter p_128933_) throws IOException {
      byte b0 = p_128931_.readByte();
      p_128933_.accountBytes(1); // Forge: Count everything!
      if (b0 == 0) {
         return EndTag.INSTANCE;
      } else {
         p_128933_.readUTF(p_128931_.readUTF()); //Forge: Count this string.
         p_128933_.accountBytes(4); //Forge: 4 extra bytes for the object allocation.

         try {
            return TagTypes.getType(b0).load(p_128931_, p_128932_, p_128933_);
         } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.addCategory("NBT Tag");
            crashreportcategory.setDetail("Tag type", b0);
            throw new ReportedException(crashreport);
         }
      }
   }
}
