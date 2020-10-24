#include <windows.h>
#include <cfapi.h>

/* https://github.com/openjdk/panama-foreign/blob/foreign-jextract/doc/panama_jextract.md */
/* https://github.com/LuaDist/libsqlite3/blob/master/sqlite3.h */
/*typedef int (*sqlite3_callback)(void*,int,char**, char**);*/

VOID ALLOCATE_CF_CALLBACK (
  VOID
	(CALLBACK *CF_CALLBACK) (
		_In_ CONST CF_CALLBACK_INFO *CallbackInfo,
		_In_ CONST CF_CALLBACK_PARAMETERS *CallbackParameters
		)
);
