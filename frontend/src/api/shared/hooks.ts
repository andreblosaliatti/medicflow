import { useCallback, useEffect, useMemo, useState } from "react";

export type QueryState<TData> = {
  data: TData;
  isLoading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
};

export function useApiQuery<TData>(
  queryKey: readonly unknown[],
  initialData: TData,
  queryFn: () => Promise<TData>,
): QueryState<TData> {
  const [data, setData] = useState<TData>(initialData);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const stableKey = useMemo(() => JSON.stringify(queryKey), [queryKey]);

  const run = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await queryFn();
      setData(response);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Erro inesperado ao carregar dados.";
      setError(message);
    } finally {
      setIsLoading(false);
    }
  }, [queryFn]);

  useEffect(() => {
    void run();
  }, [run, stableKey]);

  return { data, isLoading, error, refetch: run };
}

export type MutationState<TVariables, TResult> = {
  mutateAsync: (variables: TVariables) => Promise<TResult | null>;
  isPending: boolean;
  error: string | null;
};

export function useApiMutation<TVariables, TResult>(
  mutationFn: (variables: TVariables) => Promise<TResult>,
): MutationState<TVariables, TResult> {
  const [isPending, setIsPending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const mutateAsync = useCallback(
    async (variables: TVariables) => {
      setIsPending(true);
      setError(null);

      try {
        return await mutationFn(variables);
      } catch (err) {
        const message = err instanceof Error ? err.message : "Erro inesperado ao executar ação.";
        setError(message);
        return null;
      } finally {
        setIsPending(false);
      }
    },
    [mutationFn],
  );

  return { mutateAsync, isPending, error };
}
