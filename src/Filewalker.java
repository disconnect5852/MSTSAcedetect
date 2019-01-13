import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Filewalker {

	private static List<Path> getFilesRecursive(Path path) throws IOException
	{
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			public boolean accept(Path file) throws IOException {
				return (Files.isDirectory(file) || file.getFileName().toString().toLowerCase().endsWith(".ace"));
			}
		};
		List<Path> inlist= new ArrayList<Path>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter);
		for (Path pth: stream) {
			if (Files.isDirectory(pth)) {
				inlist.addAll(getFilesRecursive(pth));
				continue;
			}
			inlist.add(pth);

		}

		return inlist;
	}
	private static void getStatistics(Path path) throws Exception {
			int bgr565=0;
			int bgra5551=0;
			int bgra4444=0;
			int dxt1=0;
			int dxt1t=0;
			int dxt3=0;
			int dxt5=0;
			int unknown=0;
			List<Path> filez= getFilesRecursive(path);
			for (Path pth: filez) {
				String format= new MSTSAcefile(pth).getTextureFormat();
				switch (format) {
				case "BGR565": bgr565++;
					break;
				case "BGRA5551": bgra5551++;
					break;
				case "BGRA4444": bgra4444++;
					break;
				case "DXT1": dxt1++;
					break;
				case "DXT1+Trans": dxt1t++;
				break;
				case "DXT3": dxt3++;
					break;
				case "DXT5": dxt5++;
					break;
				default: unknown++;
					break;
				}
			}
			System.out.println(String.format("BGR565: %1$d, BGRA5551: %2$d, BGRA4444: %3$d, DXT1: %4$d, DXT1+Trans: %5$d, DXT3: %6$d, DXT5: %7$d, UNKNOWN: %8$d", bgr565,bgra5551,bgra4444,dxt1,dxt1t,dxt3,dxt5,unknown) );
	}
	private static void getFileList(Path path, Writer writer, String filter) throws Exception {
		List<Path> filez= getFilesRecursive(path);
		for (Path pth: filez) {
			MSTSAcefile ace= new MSTSAcefile(pth);
			String format= ace.getTextureFormat();
			if (format.equalsIgnoreCase(filter)) {
				writer.write(pth.toAbsolutePath().toString()+System.getProperty("line.separator"));
			}
		}
	}
	public static void main(String[] args) {
		Path path;
		if (args.length>1) {
			String filter=args[0];
			path=Paths.get(args[1]);
			try {
				BufferedWriter bw= Files.newBufferedWriter(Paths.get("output.txt"), StandardCharsets.UTF_8, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
				getFileList(path, bw, filter);
				bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		} else if (args.length==1) {
			path=Paths.get(args[0]);
			try {
				getStatistics(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Not enough parameters.");
			System.out.println("'acechecker.jar <path>' to make statistics of the files on <path>");
			System.out.println("'acechecker.jar <filter> <path>' to make list of ACE files which compression matches the <filter> possible filters: DXT1 ,DXT3 , DXT5, BGR565, BGRA5551, or BGRA4444");
		}

	}

}
