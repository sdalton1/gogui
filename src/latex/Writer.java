//-----------------------------------------------------------------------------
// $Id$
// $Source$
//-----------------------------------------------------------------------------

package latex;

//-----------------------------------------------------------------------------

import java.io.*;
import java.text.*;
import java.util.*;
import go.*;

//-----------------------------------------------------------------------------

public class Writer
{
    public static class Error extends Exception
    {
        public Error(String s)
        {
            super(s);
        }
    }    

    public Writer(File file, Board board, String application, String version,
                  int handicap, String playerBlack, String playerWhite,
                  String gameComment, Score score)
        throws FileNotFoundException
    {        
        FileOutputStream out = new FileOutputStream(file);
        m_out = new PrintStream(out);
        m_board = board;
        m_file = file;
        printBeginDocument();
        printBeginPSGo();
        printInternalMoves();
        printMoves();
        printEndPSGo();
        printEndDocument();
        m_out.close();
    }

    public Writer(File file, Board board, String application, String version)
        throws FileNotFoundException
    {        
        FileOutputStream out = new FileOutputStream(file);
        m_out = new PrintStream(out);
        m_board = board;
        m_file = file;
        printBeginDocument();
        printBeginPSGo();
        printPosition();
        printEndPSGo();
        String toMove =
            (m_board.getToMove() == Color.BLACK ? "Black" : "White");
        m_out.println("\n\\begin{center}\n" +
                      toMove + " to move.\n" +
                      "\\end{center}");
        printEndDocument();
        m_out.close();
    }

    private File m_file;

    private PrintStream m_out;

    private Board m_board;

    private void printBeginDocument()
    {
        m_out.println("\\documentclass{article}\n" +
                      "\\usepackage{psgo}\n" +
                      "\\pagestyle{empty}\n" +
                      "\\begin{document}\n");
    }

    private void printBeginPSGo()
    {
        m_out.println("\\begin{psgoboard}[" + m_board.getSize() + "]");
    }

    private void printColor(Color color)
    {
        if (color == Color.BLACK)
            m_out.print("{black}");
        else
        {
            assert(color == Color.WHITE);
            m_out.print("{white}");
        }
    }

    private void printCoordinates(Point p)
    {
        String s = p.toString();
        m_out.print("{" + s.substring(0, 1).toLowerCase() + "}{" +
                    s.substring(1) + "}");
    }

    private void printEndDocument()
    {
        m_out.println("\n\\end{document}");
    }

    private void printEndPSGo()
    {
        m_out.println("\\end{psgoboard}");
    }

    private void printInternalMoves()
    {
        for (int i = 0; i < m_board.getInternalNumberMoves(); ++i)
        {
            Move move = m_board.getInternalMove(i);
            Point point = move.getPoint();
            if (point != null)
                printStone(move.getColor(), point);
        }
    }

    private void printMoves()
    {
        m_out.println("\\setcounter{gomove}{0}");
        for (int i = 0; i < m_board.getNumberSavedMoves(); ++i)
        {
            Move move = m_board.getMove(i);
            Point point = move.getPoint();
            Color color = move.getColor();
            if (point == null
                || (i % 2 == 0 && color != Color.BLACK)
                || (i % 2 == 1 && color != Color.WHITE))
                break;
            m_out.print("\\move");
            printCoordinates(point);
            m_out.print("\n");
        }
    }

    private void printPosition()
    {
        int numberPoints = m_board.getNumberPoints();
        for (int i = 0; i < numberPoints; ++i)
        {
            Point point = m_board.getPoint(i);
            Color color = m_board.getColor(point);
            if (color != Color.EMPTY)
                printStone(color, point);
        }
    }

    void printStone(Color color, Point point)
    {
        m_out.print("\\stone");
        printColor(color);
        printCoordinates(point);
        m_out.print("\n");
    }
}

//-----------------------------------------------------------------------------
