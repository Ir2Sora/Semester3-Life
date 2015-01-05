Semester3-Life
=======================

## How to run (on local machine)
```sbt "run <src-data> <output> <number-of-iterations> <addresses-data>"```

src-data - Путь к файлу, содержащий исходные данные
output - Путь по которому будут сохранены результаты
number-of-iterations - Количество итераций игры
addresses-data - Путь к файлу который содержит информацию об адресах удаленных машин в следующем формате:
```
<system-name> <host-name> <port>
<system-name> <host-name> <port>
```

Пример:
```
AkkaSystem1 127.0.0.1 7295
AkkaSystem2 127.0.0.1 7251
```

### Output
#### Example output:
```
width 10000
height 10000
cells 100000000
iterations 5
duration 6.316005889 sec
```

## How to run (on remote machine)
```sbt "remote <system-name> <host-name> <port>"```

