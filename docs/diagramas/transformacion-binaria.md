# Transformación del árbol general a binario

Regla: el **primer hijo** de cada unidad pasa a ser su hijo izquierdo y el **siguiente hermano** pasa a ser su hermano derecho. La raíz nunca tiene hermano derecho.

## Árbol general (después de crear SW, SW-N3 y SW-N3-A)

```mermaid
flowchart TB
UTA --> FCHE
FCHE --> EB
FCHE --> SW
EB --> EBN1["EB-N1"]
SW --> SWN3["SW-N3"]
EBN1 --> EBN1A["EB-N1-A"]
EBN1 --> EBN1B["EB-N1-B"]
SWN3 --> SWN3A["SW-N3-A"]
```

![Árbol general](arbol-general.png)

## Árbol binario (hijo izquierdo / hermano derecho)

```mermaid
flowchart TB
UTA -->|"hijo izq"| FCHE
FCHE -->|"hijo izq"| EB
EB -->|"hijo izq"| EBN1["EB-N1"]
EB -->|"hermano der"| SW
EBN1 -->|"hijo izq"| EBN1A["EB-N1-A"]
EBN1A -->|"hermano der"| EBN1B["EB-N1-B"]
SW -->|"hijo izq"| SWN3["SW-N3"]
SWN3 -->|"hijo izq"| SWN3A["SW-N3-A"]
```

![Árbol binario](arbol-binario.png)

Estas relaciones coinciden con la salida real de `transformarABinario()` (ver `docs/evidencias/reto/01_opcion8_ejecucion.txt`): `Hermano Der de EB: SW` y `Hermano Der de EB-N1-A: EB-N1-B`.
