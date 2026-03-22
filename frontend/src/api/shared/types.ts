export type PageResponse<TItem> = {
  content: TItem[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};

export function mapPageResponse<TSource, TTarget>(
  page: PageResponse<TSource>,
  mapper: (item: TSource) => TTarget,
): PageResponse<TTarget> {
  return {
    ...page,
    content: page.content.map(mapper),
  };
}

export function emptyPageResponse<TItem>(overrides: Partial<PageResponse<TItem>> = {}): PageResponse<TItem> {
  return {
    content: [],
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0,
    first: true,
    last: true,
    empty: true,
    ...overrides,
  };
}
