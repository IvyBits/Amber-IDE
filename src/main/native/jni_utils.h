#define JGetFieldID(env, self, name, sig) \
        env->GetFieldID(env->GetObjectClass(self), name, sig)
#define JGetField(env, self, type, name, sig) \
        env->Get##type##Field(self, JGetFieldID(env, self, name, sig))
#define JSetField(env, self, type, name, sig, value) \
        env->Set##type##Field(self, JGetFieldID(env, self, name, sig), value)

#define JGetString(env, self, name) \
        env->GetStringChars((jstring) JGetField(env, self, Object, name, "Ljava/lang/String;"), NULL)
#define JGetLong(env, self, name) \
        JGetField(env, self, Long, name, "J")
#define JGetInt(env, self, name) \
        JGetField(env, self, Int, name, "I")
#define JGetBool(env, self, name) \
        JGetField(env, self, Boolean, name, "Z")

#define JSetLong(env, self, name, value) \
        JSetField(env, self, Long, name, "J", value)
#define JSetInt(env, self, name, value) \
        JSetField(env, self, Int, name, "I", value)
#define JSetBool(env, self, name, value) \
        JSetField(env, self, Boolean, name, "Z", value)