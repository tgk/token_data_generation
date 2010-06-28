import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.*;
import java.util.*;

import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.iterator.*;

/**
 *  Generates a fingerprint for a given AtomContainer. Fingerprints are
 *  one-dimensional bit arrays, where bits are set according to a the occurrence
 *  of a particular structural feature (See for example the Daylight inc. theory
 *  manual for more information). Fingerprints allow for a fast screening step to
 *  exclude candidates for a substructure search in a database. They are also a
 *  means for determining the similarity of chemical structures. <p>
 *
 *  A fingerprint is generated for an AtomContainer with this code: <pre>
 *   Molecule molecule = new Molecule();
 *   BitSet fingerprint = Fingerprinter.getFingerprint(molecule);
 *   fingerprint.size(); // returns 1024 by default
 *   fingerprint.length(); // returns the highest set bit
 * </pre> <p>
 *
 *  The FingerPrinter assumes that hydrogens are explicitly given! Furthermore, if
 *  pseudo atoms or atoms with malformed symbols are present, their atomic number is
 *  taken as one more than the last element currently supported in {@link org.openscience.cdk.config.Symbols}. 
 *
 *  <font color="#FF0000">Warning: The aromaticity detection for this
 *  FingerPrinter relies on AllRingsFinder, which is known to take very long
 *  for some molecules with many cycles or special cyclic topologies. Thus, the
 *  AllRingsFinder has a built-in timeout of 5 seconds after which it aborts and
 *  throws an Exception. If you want your SMILES generated at any expense, you
 *  need to create your own AllRingsFinder, set the timeout to a higher value,
 *  and assign it to this FingerPrinter. In the vast majority of cases,
 *  however, the defaults will be fine. </font> <p>
 *
 *  <font color="#FF0000">Another Warning : The daylight manual says:
 *  "Fingerprints are not so definite: if a fingerprint indicates a pattern is
 *  missing then it certainly is, but it can only indicate a pattern's presence
 *  with some probability." In the case of very small molecules, the probability
 *  that you get the same fingerprint for different molecules is high. </font>
 *  </p>
 *
 * @author         steinbeck
 * @cdk.created    2002-02-24
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 */
public class Fingerprinter
{
	/** The default search depth used to create the fingerprints. */
	public final static int DEFAULT_SEARCH_DEPTH = 8;
	
	private int searchDepth;



	private static final Map<String, String> queryReplace 
        = new HashMap<String, String>()
    {
	    private static final long serialVersionUID = 7647645764589L;

	    {
	        put("Cl", "X");    put("Br", "Z");
	        put("Si", "Y");    put("As", "D");
	        put("Li", "L");    put("Se", "E");
	        put("Na", "G");    put("Ca", "J");
	        put("Al", "A");
	    }
	};


	
    /**
	 * Creates a fingerprint generator of length <code>DEFAULT_SIZE</code>
	 * and with a search depth of <code>DEFAULT_SEARCH_DEPTH</code>.
	 */
	public Fingerprinter()
    {
		this(DEFAULT_SEARCH_DEPTH);
	}


	
	/**
	 * Constructs a fingerprint generator that creates fingerprints of
	 * the given size, using a generation algorithm with the given search
	 * depth.
	 *
	 * @param  size        The desired size of the fingerprint
	 * @param  searchDepth The desired depth of search
	 */
	public Fingerprinter(int searchDepth)
    {
		this.searchDepth = searchDepth;
    }



    /**
     * Generates a fingerprint of the default size for the given AtomContainer.
     *
     *@param     container         The AtomContainer for which a Fingerprint is generated
     * @exception CDKException  if there is a timeout in ring or aromaticity perception
     * @return A {@link BitSet} representing the fingerprint
     */
    public Set<Integer> getFingerprint(IAtomContainer container) throws CDKException
    {
		int position = -1;
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
		CDKHueckelAromaticityDetector.detectAromaticity(container);

        return findPaths(container, searchDepth);
	}



    /**
     * Get all paths of lengths 0 to the specified length.
     *
     * This method will find all paths upto length N starting from each
     * atom in the molecule and return the unique set of such paths.
     *
     * @param container The molecule to search
     * @param searchDepth The maximum path length desired
     * @return A Map of path strings, keyed on themselves
     */
    protected Set<Integer> findPaths(IAtomContainer container, int searchDepth)
    {
        Set<Integer> hashes = new HashSet<Integer>();

        Map<IAtom, Map<IAtom, IBond>> cache 
            = new HashMap<IAtom, Map<IAtom,IBond>>();
        
        for(IAtom startAtom : container.atoms())
        {
            List<List<IAtom>> p = PathTools.getPathsOfLengthUpto(container, startAtom, searchDepth);
            for(List<IAtom> path : p)
            {
                StringBuffer sb = new StringBuffer();
                IAtom x = path.get(0);

                // TODO if we ever get more than 255 elements, this will fail
                // maybe we should use 0 for pseudo atoms and malformed symbols?
                if(x instanceof IPseudoAtom)
                    sb.append((char) Symbols.byAtomicNumber.length + 1);
                else
                {
                    Integer atnum = Symbols.getAtomicNumber(x.getSymbol());
                    if (atnum != null) sb.append((char) atnum.intValue());
                    else sb.append((char) Symbols.byAtomicNumber.length + 1);
                }

                for(int i = 1; i < path.size(); i++)
                {
                    IAtom y = path.get(i);
                    Map<IAtom, IBond> m = cache.get(x);
                    IBond b = m != null ? m.get(y) : null;
                    if(b == null)
                    {
                        b = container.getBond(x, y);
                        HashMap<IAtom, IBond> newCache = new HashMap<IAtom, IBond>();
                        newCache.put(y, b);
                        cache.put(x, newCache);
                    }

                    sb.append(getBondSymbol(b));
                    sb.append(convertSymbol(y.getSymbol()));
                    x = y;
                }

                // we store the lexicographically lower one of the
                // string and its reverse
                StringBuffer revForm = new StringBuffer(sb);
                revForm.reverse();
                if(sb.toString().compareTo(revForm.toString()) <= 0)
                    hashes.add(sb.toString().hashCode());
                else
                    hashes.add(revForm.toString().hashCode());
            }
        }

        return hashes;
    }



    private String convertSymbol(String symbol)
    {
        String returnSymbol = queryReplace.get( symbol );
        return returnSymbol == null ? symbol
            : returnSymbol;
    }



	/**
	 *  Gets the bondSymbol attribute of the Fingerprinter class
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The bondSymbol value
	 */
	protected String getBondSymbol(IBond bond)
	{
		String bondSymbol = "";
		if (bond.getFlag(CDKConstants.ISAROMATIC))
		{
			bondSymbol = ":";
		} else if (bond.getOrder() == IBond.Order.SINGLE)
		{
			bondSymbol = "-";
		} else if (bond.getOrder() == IBond.Order.DOUBLE)
		{
			bondSymbol = "=";
		} else if (bond.getOrder() == IBond.Order.TRIPLE)
		{
			bondSymbol = "#";
		}
		return bondSymbol;
	}



    public int getSearchDepth()
    {
		return searchDepth;
	}



    public static void main(String[] args) throws IOException, CDKException
    {
        Fingerprinter fingerprinter = new Fingerprinter();

        if(args.length == 0)
        {
            System.out.println("Usage: java Fingerprinter sdf-filenames...");
            return;
        }

        for(String arg : args)
        {
            File sdfFile = new File(arg);
            IteratingMDLReader reader =
                new IteratingMDLReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance());

            while(reader.hasNext())
            {
                IMolecule molecule = (IMolecule)reader.next();
                Set<Integer> fingerprint = fingerprinter.getFingerprint(molecule);
                String sep = "";
                for(Integer i : fingerprint)
                {
                    System.out.print(sep + i);
                    sep = ",";
                }
                System.out.println();
            }
        }
    }
}

