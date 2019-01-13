import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.InflaterInputStream;


public class MSTSAcefile {
	private Path path;
	private String textureFormat;
	private int channels;
	
	public MSTSAcefile(Path path) throws Exception {
		super();
		this.path = path;
		readACE();
	}
	public Path getPath() {
		return path;
	}
	public String getTextureFormat() {
		return textureFormat;
	}
	public int getChannels() {
		return channels;
	}
	private void readACE() throws Exception {
		BufferedInputStream bis= new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ));
		DataInputStream dis= new DataInputStream(bis);
		String signature=readASCIIChars(dis,8);
		if (signature.equals("SIMISA@F")) {
			//if the file is zlib compressed
			dis.skipBytes(4);
			signature=readASCIIChars(dis,4);
			if (!signature.equals("@@@@")) throw new Exception("Incorrect signature; expected '@@@@'");
			dis=new DataInputStream(new InflaterInputStream(bis));
		} else if (signature.equals("SIMISA@@")) {
			//if the file is not compressed
			signature=readASCIIChars(dis,8);
			if (!signature.equals("@@@@@@@@")) throw new Exception("Incorrect signature; expected '@@@@@@@@'");
		}
		dis.skipBytes(16);
		int surfaceFormat= dis.readInt();
		channels= dis.readByte();
		dis.close();
		switch (surfaceFormat) {
			case 234881024: textureFormat="BGR565";
				break;
			case 268435456: textureFormat="BGRA5551";
				break;
			case 285212672: textureFormat="BGRA4444";
				break;
			case 301989888: textureFormat="DXT1";
					if (channels>3) {
						textureFormat=textureFormat+"+Trans";
					}
				break;
			case 335544320: textureFormat="DXT3";
				break;
			case 369098752: textureFormat="DXT5";
				break;
			default: textureFormat="unknown";
					break;
		}
	}
	private String readASCIIChars(DataInputStream is ,int length) throws IOException {
		//java always expects UTF-8 so this is needed
		byte[] chars= new byte[length];
		is.read(chars);
		return new String (chars,"ASCII");
	}

	/*public static int solve(int x){
	    int y=0;
	    int i=0;

	    while (x>0){
	        y+=(x%10)*Math.pow(16,i);
	        x/=10;
	        i++;
	    }
	    return y;
	}*/
	/*public static void main(String[] args) {
		//MSTSAcefile msace=new MSTSAcefile(Paths.get("f:/Gamez/Openrails/Add on/overheadwire.ace"));
		//System.out.println(Integer.toHexString(50331648));
		//System.out.println(solve(11000000));
		MSTSAcefile msace=new MSTSAcefile(Paths.get("e:/1216a.ace"));
		try {
			msace.readACE();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
}
