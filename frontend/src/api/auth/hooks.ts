import { useApiMutation } from "../shared/hooks";
import { login } from "./service";
import type { AuthSessionViewModel, LoginRequest } from "./types";

export function useLoginMutation() {
  return useApiMutation<LoginRequest, AuthSessionViewModel>(login);
}
