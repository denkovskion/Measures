# Measures

Measures is a mate search chess program.

## Usage

Java 8 or later and Groovy 2.4.x are required.

```
"path\to\java.exe" -cp "path\to\Measures.jar;path\to\groovy-2.4.21.jar" blog.art.chess.measures.Main
```

```
"path\to\groovy.bat" -cp "path/to/Measures.jar" -e "blog.art.chess.measures.Main.main()"
```

Measures uses the [Universal Chess Interface](https://chessprogramming.org/UCI) protocol with a minimal subset of
commands: `uci`, `isready`, `position fen <fenstring>`, `go mate <x>`, `go perft <x>`, `quit`.

## Example

> Sam Loyd, The Gambit 1859

### Input

```
position fen 5K2/8/8/8/8/4N1pQ/5pr1/5qk1 w - - 0 1
go mate 2
```

### Output

```
info score mate 2 pv f8e7 f1e1 h3g2
bestmove f8e7
```

## Author

Ivan Denkovski is the author of Measures.
