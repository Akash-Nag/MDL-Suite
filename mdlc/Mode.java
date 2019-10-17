/**
 * Maze Description Language Compiler
 * Distributed under Artistic License 2.0
 * 
 * Compiles mazes given in MDL format to beautiful maze images or Java/Python code
 * 
 * @author	Akash Nag
 * @version	1.0
 */

package mdlc;

/**
 * Defines the output mode for the compiler
 */
enum Mode
{
	JAVA_INT, JAVA_CHAR, JAVA_BOOLEAN,
	PYTHON_INT, PYTHON_CHAR, PYTHON_BOOLEAN,
	PNG, JPG, TIF, BMP;
}