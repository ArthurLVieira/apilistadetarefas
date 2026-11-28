DO $$
DECLARE
    constraint_to_drop RECORD;
BEGIN
    IF to_regclass('public.category') IS NULL THEN
        RETURN;
    END IF;

    FOR constraint_to_drop IN
        SELECT constraint_info.conname
        FROM pg_constraint constraint_info
        WHERE constraint_info.conrelid = 'public.category'::regclass
          AND constraint_info.contype = 'u'
          AND cardinality(constraint_info.conkey) = 1
          AND (
              SELECT attribute_info.attname
              FROM pg_attribute attribute_info
              WHERE attribute_info.attrelid = constraint_info.conrelid
                AND attribute_info.attnum = constraint_info.conkey[1]
          ) IN ('name', 'color')
    LOOP
        EXECUTE format('ALTER TABLE public.category DROP CONSTRAINT %I', constraint_to_drop.conname);
    END LOOP;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'public.category'::regclass
          AND conname = 'uk_category_user_name'
    ) THEN
        ALTER TABLE public.category
            ADD CONSTRAINT uk_category_user_name UNIQUE (user_id, name);
    END IF;
END $$;