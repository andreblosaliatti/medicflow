import { useApiMutation } from "../shared/hooks";
import { login } from "./service";
import type { LoginRequest, SessionUserViewModel } from "./types";

export function useLoginMutation() {
  return useApiMutation<LoginRequest, SessionUserViewModel>(login);
}
