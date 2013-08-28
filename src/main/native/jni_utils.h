#define JGetFieldID(env, obj, name, sig) \
        env->GetFieldID(env->GetObjectClass(obj), name, sig)
#define JGetField(env, obj, type, name, sig) \
        env->Get##type##Field(obj, JGetFieldID(env, obj, name, sig))
#define JSetField(env, obj, type, name, sig, value) \
        env->Set##type##Field(obj, JGetFieldID(env, obj, name, sig), value)

#define JGetString(env, obj, name) \
        ((jstring) JGetField(env, obj, Object, name, "Ljava/lang/String;"))
#define JGetLPWSTR(env, str) ((LPWSTR) env->GetStringChars((jstring) (str), NULL))
#define JFreeLPWSTR(env, str, arr) env->ReleaseStringChars((jstring) (str), (jchar *)(arr))
#define JSetString(env, obj, name, value) \
        JSetField(env, obj, Object, name, "Ljava/lang/String;", (jstring) (value))

#define JNewLPWSTR(env, str) env->NewString((jchar*)(str), lstrlenW((LPWSTR)(str)))

#define JGetLong(env, obj, name) \
        JGetField(env, obj, Long, name, "J")
#define JGetInt(env, obj, name) \
        JGetField(env, obj, Int, name, "I")
#define JGetBool(env, obj, name) \
        JGetField(env, obj, Boolean, name, "Z")
#define JGetObject(env, obj, name, type) \
        JGetField(env, obj, Object, name, type)

#define JSetLong(env, obj, name, value) \
        JSetField(env, obj, Long, name, "J", value)
#define JSetInt(env, obj, name, value) \
        JSetField(env, obj, Int, name, "I", value)
#define JSetBool(env, obj, name, value) \
        JSetField(env, obj, Boolean, name, "Z", value)
#define JSetObject(env, obj, name, type, value) \
        JSetField(env, obj, Object, name, type, value)
