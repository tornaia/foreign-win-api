rd /s /q src\main\java\com\github\tornaia\foreign\win\api\combaseapi\internal
jextract --source -C --verbose -J-Xmx16G --filter combaseapi -d src\main\java --target-package com.github.tornaia.foreign.win.api.searchapi.internal combaseapi.h